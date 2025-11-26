package nl.fontys.s3.back_end.repository.repositoryInterface;

import nl.fontys.s3.back_end.entity.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListingStatusRepository extends JpaRepository<ListingStatus, Long> {
    Optional<ListingStatus> findByCode(String code);
}
