package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface ListingRepository
        extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing>,
        ListingRepositoryCustom {

    Optional<Listing> findBySourceCodeAndExternalId(String sourceCode, String externalId);
}