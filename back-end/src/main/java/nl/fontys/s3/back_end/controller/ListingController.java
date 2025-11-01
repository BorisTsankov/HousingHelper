package nl.fontys.s3.back_end.controller;
import nl.fontys.s3.back_end.dto.ListingsResponse;
import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.service.ListingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public ResponseEntity<ListingsResponse<PropertyDto>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @PageableDefault(size = 12, sort = "lastSeenAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Sort safeSort = whitelistSort(pageable.getSort());
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);

        Page<PropertyDto> page = listingService.list(q, type, city, minPrice, maxPrice, safePageable);
        return ResponseEntity.ok(new ListingsResponse<>(page.getContent(), page.getTotalElements()));
    }

    private Sort whitelistSort(Sort requested) {
        String[] allowed = {"lastSeenAt", "rentAmount", "areaM2"};
        Sort.Order fallback = Sort.Order.desc("lastSeenAt");
        if (requested == null || requested.isUnsorted()) return Sort.by(fallback);

        Sort result = Sort.unsorted();
        for (Sort.Order o : requested) {
            for (String col : allowed) {
                if (col.equals(o.getProperty())) {
                    result = result.and(Sort.by(new Sort.Order(o.getDirection(), o.getProperty())));
                    break;
                }
            }
        }
        return result.isUnsorted() ? Sort.by(fallback) : result;
    }
}