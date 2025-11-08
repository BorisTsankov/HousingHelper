package nl.fontys.s3.back_end.repository;

import nl.fontys.s3.back_end.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ListingRepository
        extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {

    @Query("""
            select distinct lower(trim(l.city))
            from Listing l
            where l.city is not null and trim(l.city) <> ''
            """)
    List<String> findAllDistinctCitiesUsed();
}
