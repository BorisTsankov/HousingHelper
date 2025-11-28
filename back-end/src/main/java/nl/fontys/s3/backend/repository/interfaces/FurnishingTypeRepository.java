package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.FurnishingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FurnishingTypeRepository extends JpaRepository<FurnishingType, Short> {
    Optional<FurnishingType> findByCode(String code);
}