package nl.fontys.s3.back_end.service;

import nl.fontys.s3.back_end.dto.PropertyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingService {
    List<PropertyDto> getFeatured(int limit);
    List<PropertyDto> search(String q, int limit);

    Page<PropertyDto> list(
            String q,
            String type,      // Apartment | House | Studio
            String city,      // location
            Integer minPrice, // normalized numbers
            Integer maxPrice,
            Pageable pageable
    );
}