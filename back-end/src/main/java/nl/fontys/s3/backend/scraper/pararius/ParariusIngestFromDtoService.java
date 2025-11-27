package nl.fontys.s3.backend.scraper.pararius;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.s3.backend.dto.ListingDto;
import nl.fontys.s3.backend.entity.*;
import nl.fontys.s3.backend.repository.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;

@Service
public class ParariusIngestFromDtoService {

    private static final Logger log = LoggerFactory.getLogger(ParariusIngestFromDtoService.class);

    private final ListingRepository listingRepository;
    private final ListingSourceRepository listingSourceRepository;
    private final ListingStatusRepository listingStatusRepository;
    private final AgencyRepository agencyRepository;
    private final ListingPhotoRepository listingPhotoRepository;
    private final RawListingRepository rawListingRepository;
    private final PropertyTypeRepository propertyTypeRepository;
    private final FurnishingTypeRepository furnishingTypeRepository;
    private final ObjectMapper objectMapper;

    public ParariusIngestFromDtoService(
            ListingRepository listingRepository,
            ListingSourceRepository listingSourceRepository,
            ListingStatusRepository listingStatusRepository,
            AgencyRepository agencyRepository,
            ListingPhotoRepository listingPhotoRepository,
            RawListingRepository rawListingRepository,
            PropertyTypeRepository propertyTypeRepository,
            FurnishingTypeRepository furnishingTypeRepository,
            ObjectMapper objectMapper
    ) {
        this.listingRepository = listingRepository;
        this.listingSourceRepository = listingSourceRepository;
        this.listingStatusRepository = listingStatusRepository;
        this.agencyRepository = agencyRepository;
        this.listingPhotoRepository = listingPhotoRepository;
        this.rawListingRepository = rawListingRepository;
        this.propertyTypeRepository = propertyTypeRepository;
        this.furnishingTypeRepository = furnishingTypeRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void ingest(List<ListingDto> dtos) {
        log.info("Starting ingest of {} Pararius listings", dtos.size());

        ListingSource source = listingSourceRepository.findByCode("PARARIUS")
                .orElseThrow(() -> {
                    log.error("ListingSource with code PARARIUS not found in DB");
                    return new IllegalStateException("ListingSource PARARIUS not found");
                });

        ListingStatus activeStatus = listingStatusRepository
                .findByCode("ACTIVE")
                .orElseThrow(() -> {
                    log.error("ListingStatus with code ACTIVE not found in DB");
                    return new IllegalStateException("ListingStatus ACTIVE not found");
                });

        ListingStatus removedStatus = listingStatusRepository
                .findByCode("REMOVED")
                .orElseThrow(() -> {
                    log.error("ListingStatus with code REMOVED not found in DB");
                    return new IllegalStateException("ListingStatus REMOVED not found");
                });

        Agency parariusAgency = agencyRepository
                .findByExternalIdAndSource("PARARIUS_AGENCY", source)
                .orElse(null);

        if (parariusAgency == null) {
            log.warn("Agency PARARIUS_AGENCY not found for source PARARIUS. Listings will have null agency.");
        }

        OffsetDateTime runStartedAt = OffsetDateTime.now();

        int processed = 0;
        for (ListingDto dto : dtos) {
            try {
                upsertOne(source, activeStatus, parariusAgency, dto);
                processed++;
            } catch (RuntimeException ex) {
                // RuntimeException instead of generic Exception → better for Sonar
                log.error("Failed to upsert listing externalId={} url={}",
                        dto.externalId(), dto.canonicalUrl(), ex);
            }
        }

        int deactivated = listingRepository.markMissingListingsInactive(
                source,
                activeStatus,
                removedStatus,
                runStartedAt
        );

        log.info("Finished ingest. Successfully processed {} / {} listings. Deactivated {} missing listings.",
                processed, dtos.size(), deactivated);
    }

    private void upsertOne(ListingSource source,
                           ListingStatus activeStatus,
                           Agency agency,
                           ListingDto dto) {

        String externalId = dto.externalId();
        String contentHash = computeContentHash(dto);

        log.debug("Upserting listing externalId={} url={}", externalId, dto.canonicalUrl());

        RawListing raw = rawListingRepository
                .findBySourceAndExternalId(source, externalId)
                .orElseGet(RawListing::new);

        raw.setSource(source);
        raw.setExternalId(externalId);
        raw.setUrl(dto.canonicalUrl());
        raw.setFetchedAt(OffsetDateTime.now());

        String payloadJson = toJson(dto);
        raw.setPayloadJson(payloadJson);
        raw.setContentHash(contentHash);

        rawListingRepository.save(raw);

        Listing listing = listingRepository
                .findBySourceCodeAndExternalId("PARARIUS", externalId)
                .orElseGet(Listing::new);

        boolean isNew = listing.getId() == null;

        listing.setSource(source);
        listing.setExternalId(externalId);
        listing.setCanonicalUrl(dto.canonicalUrl());
        listing.setStatus(activeStatus);
        listing.setAgency(agency);

        listing.setTitle(dto.title());
        listing.setDescription(dto.description());

        listing.setRentAmount(dto.rentAmount());
        listing.setDeposit(dto.deposit());
        listing.setAreaM2(dto.areaM2());
        listing.setRooms(dto.rooms());
        listing.setBedrooms(dto.bedrooms());
        listing.setBathrooms(dto.bathrooms());

        listing.setAvailableFrom(dto.availableFrom());
        listing.setAvailableUntil(dto.availableUntil());
        listing.setMinimumLeaseMonths(dto.minimumLeaseMonths());

        listing.setCountry(dto.country());
        listing.setCity(dto.city());
        listing.setPostalCode(dto.postalCode());
        listing.setStreet(dto.street());
        listing.setHouseNumber(dto.houseNumber());
        listing.setUnit(dto.unit());
        listing.setLat(dto.lat());
        listing.setLon(dto.lon());

        listing.setEnergyLabel(dto.energyLabel());

        if (dto.propertyType() != null) {
            propertyTypeRepository.findByCode(dto.propertyType())
                    .ifPresent(listing::setPropertyType);
        }

        if (dto.furnishingType() != null) {
            furnishingTypeRepository.findByCode(dto.furnishingType())
                    .ifPresent(listing::setFurnishingType);
        }

        if (isNew && !listing.isPetsAllowed()) {
            listing.setPetsAllowed(false);
        }

        listing.setContentHash(contentHash);

        if (dto.photoUrls() != null && !dto.photoUrls().isEmpty()) {
            listing.setPrimaryPhotoUrl(dto.photoUrls().get(0));
            listing.setPhotosCount(dto.photoUrls().size());
        } else {
            listing.setPrimaryPhotoUrl(dto.image());
            listing.setPhotosCount(dto.photosCount());
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (listing.getFirstSeenAt() == null) {
            listing.setFirstSeenAt(now);
        }
        listing.setLastSeenAt(now);

        Listing saved = listingRepository.save(listing);

        if (dto.photoUrls() != null) {
            short pos = 0;
            for (String url : dto.photoUrls()) {
                try {
                    ListingPhoto photo = new ListingPhoto();
                    photo.setListing(saved);
                    photo.setPhotoUrl(url);
                    photo.setPosition(pos++);
                    listingPhotoRepository.save(photo);
                } catch (DataAccessException e) {
                    // more specific than generic Exception
                    log.warn("Failed to save photo for listingId={} url={}", saved.getId(), url, e);
                }
            }
        }

        log.debug("Upsert complete for externalId={} (id={})", externalId, saved.getId());
    }

    private String toJson(ListingDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ListingDto to JSON for externalId={} url={}",
                    dto.externalId(), dto.canonicalUrl(), e);

            throw new ListingSerializationException(
                    "Failed to serialize ListingDto for externalId=" + dto.externalId(),
                    e
            );
        }
    }

    private String computeContentHash(ListingDto dto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            String raw = (dto.canonicalUrl() == null ? "" : dto.canonicalUrl())
                    + "|" + (dto.title() == null ? "" : dto.title())
                    + "|" + (dto.city() == null ? "" : dto.city())
                    + "|" + (dto.rentAmount() == null ? "" : dto.rentAmount().toPlainString());

            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to compute content hash for externalId={} url={}",
                    dto.externalId(), dto.canonicalUrl(), e);

            throw new ContentHashComputationException(
                    "Failed to compute content hash for externalId=" + dto.externalId(),
                    e
            );
        }
    }
}
