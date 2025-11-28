package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.ListingPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingPhotoRepository extends JpaRepository<ListingPhoto, Long> {
}
