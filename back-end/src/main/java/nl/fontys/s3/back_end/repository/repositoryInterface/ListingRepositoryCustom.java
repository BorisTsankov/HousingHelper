package nl.fontys.s3.back_end.repository.repositoryInterface;

import java.util.List;

public interface ListingRepositoryCustom {
    List<String> findAllDistinctCitiesUsed();
}