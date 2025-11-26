package nl.fontys.s3.back_end.repository.repositoryInterface;

import nl.fontys.s3.back_end.entity.Agency;
import nl.fontys.s3.back_end.entity.ListingSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    Optional<Agency> findByExternalIdAndSource(String externalId, ListingSource source);
}