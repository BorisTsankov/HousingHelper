package nl.fontys.s3.back_end.repository.repositoryInterface;

import nl.fontys.s3.back_end.entity.ListingSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ListingSourceRepository extends JpaRepository<ListingSource, Long> {
    Optional<ListingSource> findByCode(String code);
}