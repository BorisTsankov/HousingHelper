package nl.fontys.s3.backend.repository.interfaces;

import nl.fontys.s3.backend.entity.ListingSource;
import nl.fontys.s3.backend.entity.ListingStatus;

import java.time.OffsetDateTime;
import java.util.List;

public interface ListingRepositoryCustom {
    List<String> findAllDistinctCitiesUsed();
    int markMissingListingsInactive(
            ListingSource source,
            ListingStatus activeStatus,
            ListingStatus inactiveStatus,
            OffsetDateTime cutoff
    );
}