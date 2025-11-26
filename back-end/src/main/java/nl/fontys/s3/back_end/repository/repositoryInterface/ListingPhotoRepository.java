package nl.fontys.s3.back_end.repository.repositoryInterface;

import nl.fontys.s3.back_end.entity.ListingPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListingPhotoRepository extends JpaRepository<ListingPhoto, Long> {
}
