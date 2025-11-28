package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.ListingSource;
import nl.fontys.s3.backend.entity.RawListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RawListingRepository extends JpaRepository<RawListing, Long> {
    Optional<RawListing> findBySourceAndExternalId(ListingSource source, String externalId);
}
