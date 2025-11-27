package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListingStatusRepository extends JpaRepository<ListingStatus, Long> {
    Optional<ListingStatus> findByCode(String code);
}
