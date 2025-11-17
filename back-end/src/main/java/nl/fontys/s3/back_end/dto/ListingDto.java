package nl.fontys.s3.back_end.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ListingDto(
        String id,
        String title,
        String image,
        String price,
        String location,

        String displayPrice,
        String displayDeposit,

        BigDecimal rentAmount,
        String rentPeriod,
        BigDecimal deposit,

        String description,
        String status,
        String propertyType,
        String furnishingType,
        String energyLabel,

        BigDecimal areaM2,
        BigDecimal rooms,
        BigDecimal bedrooms,
        BigDecimal bathrooms,

        LocalDate availableFrom,
        LocalDate availableUntil,
        Integer minimumLeaseMonths,

        String country,
        String city,
        String postalCode,
        String street,
        String houseNumber,
        String unit,

        Double lat,
        Double lon,

        Integer photosCount,
        String canonicalUrl,

        String externalId,
        String source
) {}
