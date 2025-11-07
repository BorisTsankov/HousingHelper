package nl.fontys.s3.back_end.mapper;

import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.model.Listing;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class PropertyMapper {
    private PropertyMapper() {}

    private static String fmtCurrency(BigDecimal amount) {
        if (amount == null) return null;
        return NumberFormat.getCurrencyInstance(Locale.GERMANY).format(amount);
    }

    public static PropertyDto toPropertyDto(Listing l) {
        String title = (l.getTitle() != null && !l.getTitle().isBlank())
                ? l.getTitle()
                : "Listing#" + l.getId();

        String image = (l.getPrimaryPhotoUrl() != null && !l.getPrimaryPhotoUrl().isBlank())
                ? l.getPrimaryPhotoUrl()
                : "https://via.placeholder.com/400x250?text=No+Image";

        String location;
        if (l.getCity() != null && !l.getCity().isBlank()) {
            location = l.getCity();
        } else if (l.getCountry() != null && !l.getCountry().isBlank()) {
            location = l.getCountry();
        } else {
            location = "Unknown";
        }

        String displayPrice = (l.getRentAmount() == null)
                ? "Price on request"
                : fmtCurrency(l.getRentAmount()) + "/mo";

        String displayDeposit = (l.getDeposit() == null)
                ? null
                : fmtCurrency(l.getDeposit());

        String rentPeriod = (l.getRentPeriod() != null) ? l.getRentPeriod().getLabel() : null;
        String propertyType = (l.getPropertyType() != null) ? l.getPropertyType().getLabel() : null;
        String furnishingType = (l.getFurnishingType() != null) ? l.getFurnishingType().getLabel() : null;
        String status = (l.getStatus() != null) ? l.getStatus().getLabel() : null;
        String source = (l.getSource() != null) ? l.getSource().getLabel() : null;

        return new PropertyDto(
                String.valueOf(l.getId()),
                title,
                image,
                displayPrice,
                location,

                displayPrice,
                displayDeposit,

                l.getRentAmount(),
                rentPeriod,
                l.getDeposit(),

                l.getDescription(),
                status,
                propertyType,
                furnishingType,
                l.getEnergyLabel(),

                l.getAreaM2(),
                l.getRooms(),
                l.getBedrooms(),
                l.getBathrooms(),

                l.getAvailableFrom(),
                l.getAvailableUntil(),
                l.getMinimumLeaseMonths(),

                l.getCountry(),
                l.getCity(),
                l.getPostalCode(),
                l.getStreet(),
                l.getHouseNumber(),
                l.getUnit(),

                l.getLat(),
                l.getLon(),

                l.getPhotosCount(),
                l.getCanonicalUrl(),

                l.getExternalId(),
                source
        );
    }
}
