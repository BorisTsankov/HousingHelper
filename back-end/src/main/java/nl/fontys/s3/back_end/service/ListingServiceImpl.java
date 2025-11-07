package nl.fontys.s3.back_end.service;

import jakarta.transaction.Transactional;
import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.mapper.PropertyMapper;
import nl.fontys.s3.back_end.model.Listing;
import nl.fontys.s3.back_end.repository.ListingRepository;
import nl.fontys.s3.back_end.spec.ListingsSpec;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(Transactional.TxType.SUPPORTS)
public class ListingServiceImpl implements ListingService {
    private static final int MAX_LIMIT = 100;
    private final ListingRepository listingRepository;

    public ListingServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public List<PropertyDto> getFeatured(int limit) {
        Pageable pageable = firstPageWithLimit(limit, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        return listingRepository.findAll(pageable)
                .map(PropertyMapper::toPropertyDto)
                .getContent();
    }

    @Override
    public List<PropertyDto> search(String q, int limit) {
        Pageable pageable = firstPageWithLimit(limit, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        Specification<Listing> spec = buildSpec(q, null, null, null, null);
        return listingRepository.findAll(spec, pageable)
                .map(PropertyMapper::toPropertyDto)
                .getContent();
    }

    @Override
    public Page<PropertyDto> list(
            String q,
            String type,
            String city,
            Integer minPrice,
            Integer maxPrice,
            Pageable pageable
    ) {
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            int tmp = minPrice;
            minPrice = maxPrice;
            maxPrice = tmp;
        }

        Pageable effectivePageable = ensureSort(pageable, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        Specification<Listing> spec = buildSpec(q, type, city, minPrice, maxPrice);

        return listingRepository.findAll(spec, effectivePageable)
                .map(PropertyMapper::toPropertyDto);
    }

    @Override
    public PropertyDto getById(long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));
        return PropertyMapper.toPropertyDto(listing);
    }


    private Pageable firstPageWithLimit(int limit, Sort sort) {
        int size = Math.max(1, Math.min(limit, MAX_LIMIT));
        return PageRequest.of(0, size, sort);
    }

    private Pageable ensureSort(Pageable pageable, Sort fallback) {
        if (pageable == null) return PageRequest.of(0, 20, fallback); // sensible default
        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), fallback);
        }
        return pageable;
    }

    private Specification<Listing> buildSpec(
            String q,
            String type,
            String city,
            Integer minPrice,
            Integer maxPrice
    ) {
        Specification<Listing> spec = ListingsSpec.containsText(q)
                .and(ListingsSpec.typeEquals(type))
                .and(ListingsSpec.cityEquals(city))
                .and(ListingsSpec.minPrice(minPrice))
                .and(ListingsSpec.maxPrice(maxPrice))
                .and(ensureDistinct());
        return spec;
    }


    private Specification<Listing> ensureDistinct() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }
            return cb.conjunction();
        };
    }
}
