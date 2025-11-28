package nl.fontys.s3.backend.dto;

import java.util.List;

public record ListingsResponse<T>(
        List<T> items,
        long total,
        int page,
        int pageSize,
        int totalPages
) {}