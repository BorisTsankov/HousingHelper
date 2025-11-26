package nl.fontys.s3.back_end.scraper.pararius;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.s3.back_end.dto.ListingDto;
import nl.fontys.s3.back_end.entity.*;
import nl.fontys.s3.back_end.repository.*;
import nl.fontys.s3.back_end.repository.repositoryInterface.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
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
    private final ObjectMapper objectMapper;

    public ParariusIngestFromDtoService(ListingRepository listingRepository,
                                        ListingSourceRepository listingSourceRepository,
                                        ListingStatusRepository listingStatusRepository,
                                        AgencyRepository agencyRepository,
                                        ListingPhotoRepository listingPhotoRepository,
                                        RawListingRepository rawListingRepository,
                                        ObjectMapper objectMapper) {
        this.listingRepository = listingRepository;
        this.listingSourceRepository = listingSourceRepository;
        this.listingStatusRepository = listingStatusRepository;
        this.agencyRepository = agencyRepository;
        this.listingPhotoRepository = listingPhotoRepository;
        this.rawListingRepository = rawListingRepository;
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

        ListingStatus activeStatus = listingStatusRepository.findByCode("ACTIVE")
                .orElseThrow(() -> {
                    log.error("ListingStatus with code ACTIVE not found in DB");
                    return new IllegalStateException("ListingStatus ACTIVE not found");
                });

        Agency parariusAgency = agencyRepository
                .findByExternalIdAndSource("PARARIUS_AGENCY", source)
                .orElse(null);

        if (parariusAgency == null) {
            log.warn("Agency PARARIUS_AGENCY not found for source PARARIUS. Listings will have null agency.");
        }

        int processed = 0;
        for (ListingDto dto : dtos) {
            upsertOne(source, activeStatus, parariusAgency, dto); // no try/catch
            processed++;
        }

        log.info("Finished ingest. Successfully processed {} / {} listings", processed, dtos.size());
    }

    private void upsertOne(ListingSource source,
                           ListingStatus activeStatus,
                           Agency agency,
                           ListingDto dto) {

        String externalId = dto.externalId();
        String contentHash = computeContentHash(dto);

        log.debug("Upserting listing externalId={} url={}", externalId, dto.canonicalUrl());

        // RAW LISTING
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

        // LISTING
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
        listing.setBedrooms(dto.bedrooms());
        listing.setBathrooms(dto.bathrooms());
        listing.setAvailableFrom(dto.availableFrom());
        listing.setCountry(dto.country());
        listing.setCity(dto.city());
        listing.setPostalCode(dto.postalCode());
        listing.setStreet(dto.street());
        listing.setHouseNumber(dto.houseNumber());
        listing.setUnit(dto.unit());
        listing.setContentHash(contentHash);

        if (dto.photoUrls() != null && !dto.photoUrls().isEmpty()) {
            listing.setPrimaryPhotoUrl(dto.photoUrls().get(0));
            listing.setPhotosCount(dto.photoUrls().size());
        } else {
            listing.setPrimaryPhotoUrl(dto.image());
            listing.setPhotosCount(dto.photosCount());
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (isNew) {
            listing.setFirstSeenAt(now);
        }
        listing.setLastSeenAt(now);

        Listing saved = listingRepository.save(listing);

        // PHOTOS (basic implementation, relies on unique constraint listing_id + photo_url)
        if (dto.photoUrls() != null) {
            short pos = 0;
            for (String url : dto.photoUrls()) {
                try {
                    ListingPhoto photo = new ListingPhoto();
                    photo.setListing(saved);
                    photo.setPhotoUrl(url);
                    photo.setPosition(pos++);
                    listingPhotoRepository.save(photo);
                } catch (Exception e) {
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
            // This is critical for the jsonb column, so we log hard and rethrow
            log.error("Failed to serialize ListingDto to JSON for externalId={} url={}",
                    dto.externalId(), dto.canonicalUrl(), e);
            throw new RuntimeException("Failed to serialize ListingDto to JSON", e);
        }
    }

    private String computeContentHash(ListingDto dto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String raw = (dto.canonicalUrl() == null ? "" : dto.canonicalUrl())
                    + "|" + (dto.title() == null ? "" : dto.title())
                    + "|" + (dto.city() == null ? "" : dto.city())
                    + "|" + (dto.rentAmount() == null ? "" : dto.rentAmount().toPlainString());
            byte[] digest = md.digest(raw.getBytes());
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            log.error("Failed to compute content hash for externalId={} url={}",
                    dto.externalId(), dto.canonicalUrl(), e);
            throw new RuntimeException("Failed to compute content hash", e);
        }
    }
}
