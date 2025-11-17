package nl.fontys.s3.back_end.repository.repositoryInterface;

import nl.fontys.s3.back_end.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface ListingRepository
        extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing>,
        ListingRepositoryCustom {
}