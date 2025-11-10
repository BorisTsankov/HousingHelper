package nl.fontys.s3.back_end.dto;

import java.time.LocalDate;

public record FilterCriteria(
        String q,
        String type,
        String city,
        Integer minPrice,
        Integer maxPrice,

        // advanced
        Integer bedroomsMin,
        Integer bathroomsMin,
        String furnished,
        String petsAllowed,
        Integer areaMin,
        Integer areaMax,
        LocalDate availableFrom
) { }