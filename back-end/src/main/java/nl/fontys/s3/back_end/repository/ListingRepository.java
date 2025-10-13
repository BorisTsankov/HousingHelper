package nl.fontys.s3.back_end.repository;

import nl.fontys.s3.back_end.model.Listing;
import nl.fontys.s3.back_end.model.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    // Use paging + sorting from Pageable (we'll sort by lastSeenAt in the service)
    List<Listing> findAllBy(Pageable pageable);

    // Text search on title OR city, with paging + sorting from Pageable
    List<Listing> findByTitleContainingIgnoreCaseOrCityContainingIgnoreCase(
            String titlePart, String cityPart, Pageable pageable
    );
}