package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.Agency;
import nl.fontys.s3.backend.entity.ListingSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    Optional<Agency> findByExternalIdAndSource(String externalId, ListingSource source);
}