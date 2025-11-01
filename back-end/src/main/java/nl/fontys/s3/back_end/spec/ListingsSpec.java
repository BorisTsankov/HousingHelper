package nl.fontys.s3.back_end.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import nl.fontys.s3.back_end.model.Listing;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ListingsSpec {
    private ListingsSpec() {}

    public static Specification<Listing> containsText(String qText) {
        if (qText == null || qText.isBlank()) return alwaysTrue();
        String like = "%" + qText.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("city")), like),
                cb.like(cb.lower(root.get("description")), like)
        );
    }

    public static Specification<Listing> cityEquals(String city) {
        if (city == null || city.isBlank()) return alwaysTrue();
        String val = city.trim().toLowerCase();
        return (root, q, cb) -> cb.equal(cb.lower(root.get("city")), val);
    }

    /**
     * Filter by PropertyType.name (Apartment, House, Studio, ...).
     * If your PropertyType uses a different field (e.g., "code" or "label"),
     * change the "name" below accordingly.
     */
    public static Specification<Listing> typeEquals(String type) {
        if (type == null || type.isBlank()) return alwaysTrue();
        String val = type.trim().toLowerCase();
        return (root, qy, cb) -> {
            Join<Object, Object> pt = root.join("propertyType", JoinType.LEFT);
            return cb.equal(cb.lower(pt.get("label")), val);
        };
    }

    public static Specification<Listing> minPrice(Integer min) {
        if (min == null) return alwaysTrue();
        BigDecimal minBd = BigDecimal.valueOf(min.longValue());
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("rentAmount"), minBd);
    }

    public static Specification<Listing> maxPrice(Integer max) {
        if (max == null) return alwaysTrue();
        BigDecimal maxBd = BigDecimal.valueOf(max.longValue());
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("rentAmount"), maxBd);
    }

    public static Specification<Listing> alwaysTrue() {
        return (root, q, cb) -> cb.conjunction();
    }
}