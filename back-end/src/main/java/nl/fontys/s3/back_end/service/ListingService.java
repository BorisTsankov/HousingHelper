package nl.fontys.s3.back_end.service;

import nl.fontys.s3.back_end.dto.FilterCriteria;
import nl.fontys.s3.back_end.dto.FilterGroup;
import nl.fontys.s3.back_end.dto.FilterOption;
import nl.fontys.s3.back_end.dto.PropertyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingService {

    // Primary
    Page<PropertyDto> list(FilterCriteria criteria, Pageable pageable);

    // Optional: keep old signature but mark as deprecated
    @Deprecated
    default Page<PropertyDto> list(String q, String type, String city, Integer minPrice, Integer maxPrice, Pageable pageable) {
        FilterCriteria c = new FilterCriteria(
                q, type, city, minPrice, maxPrice,
                null, null, null, null,
                null, null, null
        );
        return list(c, pageable);
    }

    List<PropertyDto> getFeatured(int limit);

    List<PropertyDto> search(String q, int limit);

    PropertyDto getById(long id);

    List<FilterOption> getCityOptions();
}
