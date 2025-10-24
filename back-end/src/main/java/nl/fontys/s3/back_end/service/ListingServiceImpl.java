// src/main/java/nl/fontys/s3/back_end/service/ListingServiceImpl.java
package nl.fontys.s3.back_end.service;

import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.mapper.PropertyMapper;
import nl.fontys.s3.back_end.model.Listing;
import nl.fontys.s3.back_end.repository.ListingRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        // "Featured" = latest listings by lastSeenAt; if you prefer firstSeenAt, swap the field
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        List<Listing> results = listingRepository.findAllBy(pageable);
        return results.stream().map(PropertyMapper::toPropertyDto).toList();
    }

    @Override
    public List<PropertyDto> search(String q, int limit) {
        String query = (q == null) ? "" : q.trim();
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        List<Listing> results =
                listingRepository.findByTitleContainingIgnoreCaseOrCityContainingIgnoreCase(query, query, pageable);
        return results.stream().map(PropertyMapper::toPropertyDto).toList();
    }
}