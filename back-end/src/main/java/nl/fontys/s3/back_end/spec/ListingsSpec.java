package nl.fontys.s3.back_end.spec;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import nl.fontys.s3.back_end.entity.Listing;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public static Specification<Listing> bedroomsMin(Integer min) {
        if (min == null) return alwaysTrue();
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("bedrooms"), min);
    }

    public static Specification<Listing> bathroomsMin(Integer min) {
        if (min == null) return alwaysTrue();
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("bathrooms"), min);
    }

    public static Specification<Listing> furnished(String val) {
        if (val == null || val.isBlank()) return alwaysTrue();
        String v = val.trim().toLowerCase();

        return (root, q, cb) -> {
            Join<Object, Object> f = root.join("furnishingType", JoinType.LEFT);
            var code = cb.upper(f.get("code")); // FURNISHED / SEMI_FURNISHED / UNFURNISHED

            // 1) Three-state from UI
            switch (v) {
                case "furnished":
                    return cb.equal(code, "FURNISHED");
                case "semi-furnished":
                case "semi_furnished": // just in case
                    return cb.equal(code, "SEMI_FURNISHED");
                case "unfurnished":
                    return cb.equal(code, "UNFURNISHED");
            }
            // unexpected value => no-op
            return cb.conjunction();
        };
    }

    public static Specification<Listing> petsAllowed(String val) {
        if (val == null || val.isBlank()) return alwaysTrue();
        String v = val.trim().toLowerCase();

        Boolean boolVal = switch (v) {
            case "yes", "true", "1" -> true;
            case "no", "false", "0" -> false;

            default -> null;
        };
        if (boolVal == null) return alwaysTrue();

        return (root, q, cb) -> cb.equal(root.get("petsAllowed"), boolVal);
    }

    // Helper that safely parses
    private static Boolean parseYesNo(String s) {
        if (s == null) return null;
        String v = s.trim().toLowerCase();
        if (v.isEmpty()) return null;
        switch (v) {
            case "yes": case "true": case "1":  return true;
            case "no":  case "false": case "0": return false;
            default: return null; // don't filter if value is unexpected
        }
    }

    public static Specification<Listing> areaMin(Integer min) {
        if (min == null) return alwaysTrue();
        return (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("areaM2"), min);
    }

    public static Specification<Listing> areaMax(Integer max) {
        if (max == null) return alwaysTrue();
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("areaM2"), max);
    }

    public static Specification<Listing> availableFrom(LocalDate from) {
        if (from == null) return alwaysTrue();
        return (root, q, cb) -> cb.lessThanOrEqualTo(root.get("availableFrom"), from);
    }
}