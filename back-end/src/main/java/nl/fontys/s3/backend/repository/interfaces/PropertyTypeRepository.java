package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.PropertyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PropertyTypeRepository extends JpaRepository<PropertyType, Short> {
    Optional<PropertyType> findByCode(String code);
}