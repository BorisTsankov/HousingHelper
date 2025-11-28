package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.ListingSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListingSourceRepository extends JpaRepository<ListingSource, Long> {
    Optional<ListingSource> findByCode(String code);
}