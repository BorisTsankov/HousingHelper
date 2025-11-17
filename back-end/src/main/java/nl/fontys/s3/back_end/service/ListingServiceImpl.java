package nl.fontys.s3.back_end.service;

import jakarta.transaction.Transactional;
import nl.fontys.s3.back_end.dto.FilterCriteria;
import nl.fontys.s3.back_end.dto.FilterOption;
import nl.fontys.s3.back_end.dto.ListingDto;
import nl.fontys.s3.back_end.mapper.ListingMapper;
import nl.fontys.s3.back_end.entity.Listing;
import nl.fontys.s3.back_end.repository.repositoryInterface.ListingRepository;
import nl.fontys.s3.back_end.service.serviceInterface.ListingService;
import nl.fontys.s3.back_end.spec.ListingsSpec;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.BreakIterator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(Transactional.TxType.SUPPORTS)
public class ListingServiceImpl implements ListingService {
    private static final int MAX_LIMIT = 100;
    private final ListingRepository listingRepository;

    public ListingServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public List<ListingDto> getFeatured(int limit) {
        Pageable pageable = firstPageWithLimit(limit, Sort.by(Sort.Direction.DESC, "lastSeenAt"));

        return listingRepository.findAll(pageable)
                .map(ListingMapper::toModel)          // ENTITY -> MODEL
                .map(ListingMapper::toListingDto)     // MODEL  -> DTO
                .getContent();
    }

    @Override
    public List<ListingDto> search(String q, int limit) {
        Pageable pageable = firstPageWithLimit(limit, Sort.by(Sort.Direction.DESC, "lastSeenAt"));

        FilterCriteria c = new FilterCriteria(
                q, null, null, null, null,
                null, null, null, null,
                null, null, null
        );
        Specification<Listing> spec = buildSpec(c);

        return listingRepository.findAll(spec, pageable)
                .map(ListingMapper::toModel)          // ENTITY -> MODEL
                .map(ListingMapper::toListingDto)     // MODEL  -> DTO
                .getContent();
    }

    @Override
    public Page<ListingDto> list(FilterCriteria criteria, Pageable pageable) {
        // normalize ranges defensively
        Integer minPrice = criteria.minPrice();
        Integer maxPrice = criteria.maxPrice();
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            int tmp = minPrice;
            minPrice = maxPrice;
            maxPrice = tmp;
        }
        Integer areaMin = criteria.areaMin();
        Integer areaMax = criteria.areaMax();
        if (areaMin != null && areaMax != null && areaMin > areaMax) {
            int tmp = areaMin;
            areaMin = areaMax;
            areaMax = tmp;
        }

        FilterCriteria normalized = new FilterCriteria(
                criteria.q(),
                criteria.type(),
                criteria.city(),
                minPrice,
                maxPrice,
                criteria.bedroomsMin(),
                criteria.bathroomsMin(),
                criteria.furnished(),
                criteria.petsAllowed(),
                areaMin,
                areaMax,
                criteria.availableFrom()
        );

        Pageable effectivePageable = ensureSort(pageable, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        Specification<Listing> spec = buildSpec(normalized);

        return listingRepository.findAll(spec, effectivePageable)
                .map(ListingMapper::toModel)          // ENTITY -> MODEL
                .map(ListingMapper::toListingDto);    // MODEL  -> DTO
    }

    @Override
    public ListingDto getById(long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        return ListingMapper.toListingDto(
                ListingMapper.toModel(listing)        // ENTITY -> MODEL -> DTO
        );
    }

    // ---------- helper methods ----------

    private Pageable firstPageWithLimit(int limit, Sort sort) {
        int size = Math.max(1, Math.min(limit, MAX_LIMIT));
        return PageRequest.of(0, size, sort);
    }

    private Pageable ensureSort(Pageable pageable, Sort fallback) {
        if (pageable == null) return PageRequest.of(0, 20, fallback);
        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), fallback);
        }
        return pageable;
    }

    private Specification<Listing> buildSpec(FilterCriteria c) {
        return ListingsSpec.containsText(c.q())
                .and(ListingsSpec.typeEquals(c.type()))
                .and(ListingsSpec.cityEquals(c.city()))
                .and(ListingsSpec.minPrice(c.minPrice()))
                .and(ListingsSpec.maxPrice(c.maxPrice()))
                .and(ListingsSpec.bedroomsMin(c.bedroomsMin()))
                .and(ListingsSpec.bathroomsMin(c.bathroomsMin()))
                .and(ListingsSpec.furnished(c.furnished()))
                .and(ListingsSpec.petsAllowed(c.petsAllowed()))
                .and(ListingsSpec.areaMin(c.areaMin()))
                .and(ListingsSpec.areaMax(c.areaMax()))
                .and(ListingsSpec.availableFrom(c.availableFrom()))
                .and(ensureDistinct());
    }

    private Specification<Listing> ensureDistinct() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            return cb.conjunction();
        };
    }

    @Override
    public List<FilterOption> getCityOptions() {
        return listingRepository.findAllDistinctCitiesUsed().stream()
                .sorted(Comparator.naturalOrder())
                .map(v -> new FilterOption(toTitleCase(v), v)) // label, value
                .toList();
    }

    private String toTitleCase(String input) {
        if (input == null) return "";
        String lower = input.trim().toLowerCase(Locale.ROOT);
        var wb = BreakIterator.getWordInstance(Locale.ROOT);
        wb.setText(lower);
        var sb = new StringBuilder(lower.length());
        int start = wb.first();
        for (int end = wb.next(); end != BreakIterator.DONE; start = end, end = wb.next()) {
            String w = lower.substring(start, end);
            if (!w.isEmpty() && Character.isLetterOrDigit(w.codePointAt(0))) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
            } else {
                sb.append(w);
            }
        }
        return sb.toString();
    }
}