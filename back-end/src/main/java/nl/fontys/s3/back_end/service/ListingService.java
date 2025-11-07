package nl.fontys.s3.back_end.service;

import nl.fontys.s3.back_end.dto.FilterCriteria;
import nl.fontys.s3.back_end.dto.PropertyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingService {

    Page<PropertyDto> list(String q, String type, String city, Integer minPrice, Integer maxPrice, Pageable pageable);

    default Page<PropertyDto> list(FilterCriteria c, Pageable pageable) {
        return list(c.q(), c.type(), c.city(), c.minPrice(), c.maxPrice(), pageable);
    }

    List<PropertyDto> getFeatured(int limit);

    List<PropertyDto> search(String q, int limit);
    PropertyDto getById(long id);



}
