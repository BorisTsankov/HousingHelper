package nl.fontys.s3.backend.mapper;

import nl.fontys.s3.backend.dto.ListingDto;
import nl.fontys.s3.backend.entity.Listing;
import nl.fontys.s3.backend.entity.ListingPhoto;
import nl.fontys.s3.backend.model.ListingModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public final class ListingMapper {
    private ListingMapper() {}

    private static String fmtCurrency(BigDecimal amount) {
        if (amount == null) return null;
        return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(amount);
    }

    // --------- ENTITY -> MODEL ------------

    public static ListingModel toModel(Listing l) {
        if (l == null) return null;

        ListingModel m = new ListingModel();
        m.setId(l.getId());
        m.setTitle(l.getTitle());
        m.setPrimaryPhotoUrl(l.getPrimaryPhotoUrl());

        m.setRentAmount(l.getRentAmount());
        m.setDeposit(l.getDeposit());
        m.setRentPeriod(
                l.getRentPeriod() != null ? l.getRentPeriod().getLabel() : null
        );

        m.setDescription(l.getDescription());
        m.setStatus(
                l.getStatus() != null ? l.getStatus().getLabel() : null
        );
        m.setPropertyType(
                l.getPropertyType() != null ? l.getPropertyType().getLabel() : null
        );
        m.setFurnishingType(
                l.getFurnishingType() != null ? l.getFurnishingType().getLabel() : null
        );
        m.setEnergyLabel(l.getEnergyLabel());

        m.setAreaM2(l.getAreaM2());
        m.setRooms(l.getRooms());
        m.setBedrooms(l.getBedrooms());
        m.setBathrooms(l.getBathrooms());

        m.setAvailableFrom(l.getAvailableFrom());
        m.setAvailableUntil(l.getAvailableUntil());
        m.setMinimumLeaseMonths(l.getMinimumLeaseMonths());

        m.setCountry(l.getCountry());
        m.setCity(l.getCity());
        m.setPostalCode(l.getPostalCode());
        m.setStreet(l.getStreet());
        m.setHouseNumber(l.getHouseNumber());
        m.setUnit(l.getUnit());

        m.setLat(l.getLat());
        m.setLon(l.getLon());

        m.setPhotosCount(l.getPhotosCount());
        m.setCanonicalUrl(l.getCanonicalUrl());

        m.setExternalId(l.getExternalId());
        m.setSourceName(
                l.getSource() != null ? l.getSource().getLabel() : null
        );

        if (l.getPhotos() != null && !l.getPhotos().isEmpty()) {
            List<String> photoUrls = l.getPhotos().stream()
                    .map(ListingPhoto::getPhotoUrl)
                    .toList();
            m.setPhotoUrls(photoUrls);
        }

        return m;
    }

    // --------- MODEL -> DTO ------------

    public static ListingDto toListingDto(ListingModel m) {
        if (m == null) return null;

        String title = (m.getTitle() != null && !m.getTitle().isBlank())
                ? m.getTitle()
                : "Listing#" + m.getId();

        String image = (m.getPrimaryPhotoUrl() != null && !m.getPrimaryPhotoUrl().isBlank())
                ? m.getPrimaryPhotoUrl()
                : "https://via.placeholder.com/400x250?text=No+Image";

        String location;
        if (m.getCity() != null && !m.getCity().isBlank()) {
            location = m.getCity();
        } else if (m.getCountry() != null && !m.getCountry().isBlank()) {
            location = m.getCountry();
        } else {
            location = "Unknown";
        }

        String displayPrice = (m.getRentAmount() == null)
                ? "Price on request"
                : fmtCurrency(m.getRentAmount()) + "/mo";

        String displayDeposit = (m.getDeposit() == null)
                ? null
                : fmtCurrency(m.getDeposit());

        return new ListingDto(
                String.valueOf(m.getId()),
                title,
                image,
                displayPrice,
                location,

                displayPrice,
                displayDeposit,

                m.getRentAmount(),
                m.getRentPeriod(),
                m.getDeposit(),

                m.getDescription(),
                m.getStatus(),
                m.getPropertyType(),
                m.getFurnishingType(),
                m.getEnergyLabel(),

                m.getAreaM2(),
                m.getRooms(),
                m.getBedrooms(),
                m.getBathrooms(),

                m.getAvailableFrom(),
                m.getAvailableUntil(),
                m.getMinimumLeaseMonths(),

                m.getCountry(),
                m.getCity(),
                m.getPostalCode(),
                m.getStreet(),
                m.getHouseNumber(),
                m.getUnit(),

                m.getLat(),
                m.getLon(),

                m.getPhotosCount(),
                m.getCanonicalUrl(),

                m.getExternalId(),
                m.getSourceName(),

                m.getPhotoUrls() != null ? m.getPhotoUrls() : List.of()        );
    }
}
