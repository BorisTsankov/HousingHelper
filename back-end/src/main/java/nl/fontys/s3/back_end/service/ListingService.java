package nl.fontys.s3.back_end.service;

import nl.fontys.s3.back_end.dto.PropertyDto;

import java.util.List;

public interface ListingService {
    List<PropertyDto> getFeatured(int limit);
    List<PropertyDto> search(String q, int limit);
}
