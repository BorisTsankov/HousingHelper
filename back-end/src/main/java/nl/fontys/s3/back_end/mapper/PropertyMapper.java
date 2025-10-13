package nl.fontys.s3.back_end.mapper;

import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.model.Listing;

import java.text.NumberFormat;
import java.util.Locale;

public final class PropertyMapper {
    private PropertyMapper() {
    }

    public static PropertyDto toPropertyDto(Listing l) {

        String price = l.getRentAmount() == null
                ? "Price on request"
                : NumberFormat.getCurrencyInstance(Locale.GERMANY)
                .format(l.getRentAmount()) + "/mo";

        String title = (l.getTitle() != null && !l.getTitle().isBlank())
                ? l.getTitle()
                : "Listing #" + l.getId();

        String location = l.getCity() != null ? l.getCity()
                : (l.getCountry() != null ? l.getCountry() : "Unknown");

        String image = (l.getPrimaryPhotoUrl() != null && !l.getPrimaryPhotoUrl().isBlank())
                ? l.getPrimaryPhotoUrl()
                : "https://via.placeholder.com/400x250?text=No+Image";

        return new PropertyDto(
                String.valueOf(l.getId()),
                title,
                image,
                price,
                location
        );
    }
}

