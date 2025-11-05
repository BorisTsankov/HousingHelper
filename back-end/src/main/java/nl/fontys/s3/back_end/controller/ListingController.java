package nl.fontys.s3.back_end.controller;
import nl.fontys.s3.back_end.dto.*;
import nl.fontys.s3.back_end.model.Listing;
import nl.fontys.s3.back_end.service.ListingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingDetailsDto> getOne(@PathVariable long id) {
        return ResponseEntity.ok(listingService.getById(id));
    }

    @GetMapping
    public ResponseEntity<ListingsResponse<PropertyDto>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,

            // advanced (listings page)
            @RequestParam(required = false) Integer bedroomsMin,
            @RequestParam(required = false) Integer bathroomsMin,
            @RequestParam(required = false) String furnished,    // "yes" | "no"
            @RequestParam(required = false) String petsAllowed,  // "yes" | "no"
            @RequestParam(required = false) Integer areaMin,
            @RequestParam(required = false) Integer areaMax,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate availableFrom,

            @PageableDefault(size = 12, sort = "lastSeenAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Sort safeSort = whitelistSort(pageable.getSort());
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);

        FilterCriteria criteria = new FilterCriteria(
                q, type, city, minPrice, maxPrice,
                bedroomsMin, bathroomsMin, furnished, petsAllowed,
                areaMin, areaMax, availableFrom
        );

        Page<PropertyDto> page = listingService.list(criteria, safePageable);
        return ResponseEntity.ok(new ListingsResponse<>(page.getContent(), page.getTotalElements()));
    }

    @GetMapping("/filters")
    public ResponseEntity<FilterGroup> filters(@RequestParam(defaultValue = "home") String scope) {
        // In real life: fetch distinct values from DB or config
        List<FilterOption> types = List.of(
                new FilterOption("Apartment", "apartment"),
                new FilterOption("House", "house"),
                new FilterOption("Studio", "studio")
        );
        List<FilterOption> cities = List.of(
                new FilterOption("Eindhoven", "eindhoven"),
                new FilterOption("Rotterdam", "rotterdam"),
                new FilterOption("Amsterdam", "amsterdam")
        );
        List<PriceBucket> price = List.of(
                new PriceBucket("$500 - $1,000", 500, 1000),
                new PriceBucket("$1,000 - $1,500", 1000, 1500),
                new PriceBucket("$2,500+", 2500, null)
        );

        if ("listings".equalsIgnoreCase(scope)) {
            List<FilterOption> bedrooms = List.of(
                    new FilterOption("Studio / 0+", "0"),
                    new FilterOption("1+", "1"),
                    new FilterOption("2+", "2"),
                    new FilterOption("3+", "3")
            );
            List<FilterOption> bathrooms = List.of(
                    new FilterOption("1+", "1"),
                    new FilterOption("2+", "2")
            );
            List<FilterOption> furnished = List.of(
                    new FilterOption("Yes", "yes"),
                    new FilterOption("No", "no")
            );
            List<FilterOption> pets = List.of(
                    new FilterOption("Allowed", "yes"),
                    new FilterOption("Not allowed", "no")
            );

            return ResponseEntity.ok(new FilterGroup(types, cities, price, bedrooms, bathrooms, furnished, pets));
        }

        // home scope: advanced lists empty
        return ResponseEntity.ok(new FilterGroup(types, cities, price,
                List.of(), List.of(), List.of(), List.of()));
    }

    private Sort whitelistSort(Sort requested) {
        String[] allowed = {"lastSeenAt", "rentAmount", "areaM2", "bedrooms"};
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