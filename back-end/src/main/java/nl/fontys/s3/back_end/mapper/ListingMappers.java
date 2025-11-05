package nl.fontys.s3.back_end.mapper;


import nl.fontys.s3.back_end.dto.*;
import nl.fontys.s3.back_end.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ListingMappers {
    private ListingMappers() {}

    // ===== Helpers =====
    private static Integer toInt(BigDecimal v) {
        return v == null ? null : v.setScale(0, RoundingMode.HALF_UP).intValue();
    }    private static String iso(Object t) {
        if (t == null) return null;
        if (t instanceof java.time.OffsetDateTime odt) return odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        if (t instanceof java.time.LocalDate ld) return ld.format(DateTimeFormatter.ISO_DATE);
        return t.toString();
    }

    private static String cityOrCountry(Listing l) {
        if (l.getCity() != null && !l.getCity().isBlank()) return l.getCity();
        if (l.getCountry() != null && !l.getCountry().isBlank()) return l.getCountry();
        return "Unknown";
    }
    private static String safeImg(Listing l) {
        String p = l.getPrimaryPhotoUrl();
        return (p != null && !p.isBlank()) ? p : "https://via.placeholder.com/400x250?text=No+Image";
    }
    private static String titleOf(Listing l) {
        String t = l.getTitle();
        return (t != null && !t.isBlank()) ? t : "Listing#" + l.getId();
    }



}
