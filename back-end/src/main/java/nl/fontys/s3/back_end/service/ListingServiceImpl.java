package nl.fontys.s3.back_end.service;

import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.mapper.PropertyMapper;
import nl.fontys.s3.back_end.model.Listing;
import nl.fontys.s3.back_end.repository.ListingRepository;
import nl.fontys.s3.back_end.spec.ListingsSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingServiceImpl implements ListingService {
    private final ListingRepository listingRepository;

    public ListingServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public List<PropertyDto> getFeatured(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        return listingRepository.findAll(pageable)
                .map(PropertyMapper::toPropertyDto)
                .getContent();
    }

    @Override
    public List<PropertyDto> search(String q, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        Specification<Listing> spec = ListingsSpec.containsText(q);
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
        Specification<Listing> spec = ListingsSpec.containsText(q)
                .and(ListingsSpec.typeEquals(type))
                .and(ListingsSpec.cityEquals(city))
                .and(ListingsSpec.minPrice(minPrice))
                .and(ListingsSpec.maxPrice(maxPrice));

        return listingRepository.findAll(spec, pageable)
                .map(PropertyMapper::toPropertyDto);
    }
}
