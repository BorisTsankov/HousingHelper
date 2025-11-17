package nl.fontys.s3.back_end.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nl.fontys.s3.back_end.repository.repositoryInterface.ListingRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ListingRepositoryImpl implements ListingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> findAllDistinctCitiesUsed() {
        return entityManager.createQuery("""
                select distinct lower(trim(l.city))
                from Listing l
                where l.city is not null and trim(l.city) <> ''
                """, String.class)
                .getResultList();
    }
}