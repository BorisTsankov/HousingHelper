package nl.fontys.s3.back_end.repository.repositoryInterface;

import nl.fontys.s3.back_end.entity.ListingSource;
import nl.fontys.s3.back_end.entity.RawListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RawListingRepository extends JpaRepository<RawListing, Long> {
    Optional<RawListing> findBySourceAndExternalId(ListingSource source, String externalId);
}
