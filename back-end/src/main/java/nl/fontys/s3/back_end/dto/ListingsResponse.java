package nl.fontys.s3.back_end.dto;

import java.util.List;

public record ListingsResponse<T>(
        List<T> items,
        long total,
        int page,
        int pageSize,
        boolean hasNext
) {}