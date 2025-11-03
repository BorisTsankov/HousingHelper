package nl.fontys.s3.back_end.mapper;

import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.model.Listing;

import java.text.NumberFormat;
import java.util.Locale;

public final class PropertyMapper {
    private PropertyMapper() {
    }

    public static PropertyDto toPropertyDto(Listing l) {

        String price;
        if(l.getRentAmount() == null){
            price = "Price on request";
        }
        else{
            price = NumberFormat.getCurrencyInstance(Locale.GERMANY).format(l.getRentAmount()) + "/mo";
        }

        String title;
        if(l.getTitle() != null && !l.getTitle().isBlank()){
            title = l.getTitle();
        }
        else {
            title = "Listing#" + l.getId();
        }

        String location;
        if(l.getCity() != null) {
            location=l.getCity();
        }
        else if(l.getCountry() != null) {
            location = l.getCountry();
        }
        else {
            location = "Unknown";
        }

        String image;
        if(l.getPrimaryPhotoUrl() != null && !l.getPrimaryPhotoUrl().isBlank()){
            image = l.getPrimaryPhotoUrl();
        }
        else {
            image = "https://via.placeholder.com/400x250?text=No+Image";
        }

        return new PropertyDto(
                String.valueOf(l.getId()),
                title,
                image,
                price,
                location
        );
    }
}

