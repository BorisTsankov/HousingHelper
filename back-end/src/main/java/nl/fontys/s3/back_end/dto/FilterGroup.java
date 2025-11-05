package nl.fontys.s3.back_end.dto;

import java.util.List;

public record FilterGroup(
        List<FilterOption> types,
        List<FilterOption> cities,
        List<PriceBucket> priceBuckets,
        // advanced
        List<FilterOption> bedrooms,
        List<FilterOption> bathrooms,
        List<FilterOption> furnished,   // e.g. yes/no/partly
        List<FilterOption> petsAllowed  // e.g. yes/no
) {}