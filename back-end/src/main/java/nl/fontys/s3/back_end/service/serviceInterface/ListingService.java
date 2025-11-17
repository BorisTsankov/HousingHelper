package nl.fontys.s3.back_end.service.serviceInterface;

import nl.fontys.s3.back_end.dto.FilterCriteria;
import nl.fontys.s3.back_end.dto.FilterOption;
import nl.fontys.s3.back_end.dto.ListingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingService {
    Page<ListingDto> list(FilterCriteria criteria, Pageable pageable);

    @Deprecated
    default Page<ListingDto> list(String q, String type, String city, Integer minPrice, Integer maxPrice, Pageable pageable) {
        FilterCriteria c = new FilterCriteria(
                q, type, city, minPrice, maxPrice,
                null, null, null, null,
                null, null, null
        );
        return list(c, pageable);
    }
    List<ListingDto> getFeatured(int limit);
    List<ListingDto> search(String q, int limit);
    ListingDto getById(long id);
    List<FilterOption> getCityOptions();
}
