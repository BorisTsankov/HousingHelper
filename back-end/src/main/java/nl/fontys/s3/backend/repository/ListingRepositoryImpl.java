package nl.fontys.s3.backend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nl.fontys.s3.backend.entity.ListingSource;
import nl.fontys.s3.backend.entity.ListingStatus;
import nl.fontys.s3.backend.repository.interfaces.ListingRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
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

    @Override
    public int markMissingListingsInactive(
            ListingSource source,
            ListingStatus activeStatus,
            ListingStatus inactiveStatus,
            OffsetDateTime cutoff
    ) {
        return entityManager.createQuery("""
                        update Listing l
                        set l.status = :inactiveStatus
                        where l.source = :source
                          and l.status = :activeStatus
                          and l.lastSeenAt < :cutoff
                        """)
                .setParameter("source", source)
                .setParameter("activeStatus", activeStatus)
                .setParameter("inactiveStatus", inactiveStatus)
                .setParameter("cutoff", cutoff)
                .executeUpdate();
    }
}

