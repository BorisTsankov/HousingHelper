package nl.fontys.s3.backend.scraper.pararius;

import nl.fontys.s3.backend.dto.ListingDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParariusScraperUsingListingDto {

    private static final Logger log = LoggerFactory.getLogger(ParariusScraperUsingListingDto.class);
    private static final Pattern POSTCODE_PATTERN = Pattern.compile("(\\d{4}\\s?[A-Z]{2})");
    private static final Pattern MONTHS_PATTERN = Pattern.compile("(\\d+)");
    private static final String BASE_URL = "https://www.pararius.com";
    private static final DateTimeFormatter FORMAT_DD_MM_YYYY =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FORMAT_D_MMMM_YYYY =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);

    /**
     * Scrapes Pararius apartments for a given city (slug) and returns ListingDto objects.
     * Example slug: "eindhoven"
     */
    public List<ListingDto> scrapeCity(String citySlug, int maxPages) {
        List<ListingDto> result = new ArrayList<>();

        log.info("Starting Pararius scrape for city='{}', maxPages={}", citySlug, maxPages);

        for (int page = 1; page <= maxPages; page++) {
            String url = BASE_URL + "/apartments/" + citySlug + "/page-" + page;
            Document doc;

            try {
                log.debug("Fetching Pararius search page {} for city='{}' -> {}", page, citySlug, url);
                doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (compatible; ParariusScraper/1.0)")
                        .timeout(15000)
                        .get();
            } catch (java.io.IOException e) {
                log.error("Failed to fetch Pararius page {} for city='{}' (url={})",
                        page, citySlug, url, e);

                throw new PageFetchException(
                        "Unable to fetch Pararius page " + page + " for city '" + citySlug + "' (url=" + url + ")",
                        e
                );
            }

            Elements items = doc.select(".listing-search-item");

            if (items.isEmpty()) {
                log.warn("No listing items found on Pararius page {} for city='{}'. Stopping pagination.", page, citySlug);
                break;
            }

            log.debug("Found {} listing elements on Pararius page {} for city='{}'", items.size(), page, citySlug);

            for (Element item : items) {
                try {
                    ListingDto dto = parseItemToDto(item);
                    if (dto != null) {
                        result.add(dto);
                    } else {
                        log.warn("parseItemToDto returned null for an item on page {} city='{}'", page, citySlug);
                    }
                } catch (RuntimeException ex) {
                    log.error("Error while parsing a listing item on page {} city='{}'", page, citySlug, ex);
                }
            }
        }

        log.info("Finished Pararius scrape for city='{}'. Total listings scraped: {}", citySlug, result.size());
        return result;
    }

    private ListingDto parseItemToDto(Element item) {
        Element linkEl = findListingLink(item);
        if (linkEl == null) {
            return null;
        }

        String href = extractHref(linkEl);
        if (href == null) {
            return null;
        }

        String fullUrl = buildFullUrl(href);
        String externalId = normalizeExternalId(href);

        String title = text(item, ".listing-search-item__title");
        String priceText = text(item, ".listing-search-item__price");
        BigDecimal rentAmount = parsePrice(priceText);

        String locationText = text(item, ".listing-search-item__location");
        LocationParts location = parseLocation(locationText);

        String imageUrl = extractPrimaryImageUrl(item);

        Detail detail = fetchDetailPage(fullUrl);

        List<String> photos = buildPhotoList(detail.photoUrls(), imageUrl);
        log.info("Listing {} photos count = {}", externalId, photos.size());

        String rentPeriodCode = determineRentPeriodCode(priceText);

        LocalDate availableFrom = detail.availableFrom();
        LocalDate availableUntil = calculateAvailableUntil(availableFrom, detail.minimumLeaseMonths());

        int photosCount = photos.size();

        return new ListingDto(
                null,
                title,
                imageUrl,
                priceText,
                locationText,

                priceText,
                detail.displayDeposit(),

                rentAmount,
                rentPeriodCode,
                detail.deposit(),

                detail.description(),
                "ACTIVE",
                detail.propertyTypeCode(),
                detail.furnishingTypeCode(),
                detail.energyLabel(),

                detail.areaM2(),
                detail.rooms(),
                detail.bedrooms(),
                detail.bathrooms(),

                availableFrom,
                availableUntil,
                detail.minimumLeaseMonths(),

                "NL",
                location.city(),
                detail.postalCode(),
                location.street(),
                detail.houseNumber(),
                null,

                detail.lat(),
                detail.lon(),

                photosCount,
                fullUrl,

                externalId,
                "PARARIUS",

                photos
        );
    }

    private Element findListingLink(Element item) {
        Element linkEl = item.selectFirst(".listing-search-item__link, a[href]");
        if (linkEl == null) {
            log.warn("Listing item without a link element encountered. Skipping.");
        }
        return linkEl;
    }

    private String extractHref(Element linkEl) {
        String href = linkEl.attr("href");
        if (href == null || href.isBlank()) {
            log.warn("Listing item has empty href attribute. Skipping.");
            return null;
        }
        return href;
    }

    private String buildFullUrl(String href) {
        return href.startsWith("http") ? href : BASE_URL + href;
    }

    private String normalizeExternalId(String href) {
        return href.replaceFirst("^/+", "").replaceAll("/$", "");
    }

    private record LocationParts(String street, String city) {}

    private LocationParts parseLocation(String locationText) {
        if (locationText == null) {
            return new LocationParts(null, null);
        }
        String[] parts = locationText.split(",");
        String street = parts.length >= 1 ? parts[0].trim() : null;
        String city = parts.length >= 2 ? parts[1].trim() : null;
        return new LocationParts(street, city);
    }

    private String extractPrimaryImageUrl(Element item) {
        Element imgEl = item.selectFirst("img");
        return imgEl != null ? imgEl.absUrl("src") : null;
    }

    private List<String> buildPhotoList(List<String> detailPhotos, String fallbackImageUrl) {
        if (detailPhotos != null && !detailPhotos.isEmpty()) {
            return detailPhotos;
        }
        if (fallbackImageUrl != null) {
            return List.of(fallbackImageUrl);
        }
        return List.of();
    }

    private String determineRentPeriodCode(String priceText) {
        if (priceText == null) {
            return null;
        }
        if (priceText.toLowerCase().contains("per month")) {
            return "PER_MONTH";
        }
        return null;
    }

    private LocalDate calculateAvailableUntil(LocalDate availableFrom, Integer minimumLeaseMonths) {
        if (availableFrom == null || minimumLeaseMonths == null) {
            return null;
        }
        return availableFrom.plusMonths(minimumLeaseMonths);
    }

    private String text(Element root, String selector) {
        Element el = root.selectFirst(selector);
        if (el == null) {
            log.trace("Selector '{}' not found in element.", selector);
        }
        return el != null ? el.text() : null;
    }

    private BigDecimal parsePrice(String text) {
        if (text == null) {
            return null;
        }
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            log.debug("Failed to parse price from text='{}'", text);
            return null;
        }
        try {
            return new BigDecimal(digits);
        } catch (NumberFormatException ex) {
            log.warn("NumberFormatException while parsing price from digits='{}' (original='{}')", digits, text, ex);
            return null;
        }
    }

    private Detail fetchDetailPage(String url) {
        log.debug("Fetching Pararius detail page: {}", url);

        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; ParariusScraper/1.0)")
                    .timeout(15000)
                    .get();
        } catch (java.io.IOException e) {
            log.error("Failed to load detail page: {}", url, e);
            throw new PageFetchException("Failed to load detail page: " + url, e);
        }

        String description;
        Element descContent = doc.selectFirst(".listing-detail-description__content");
        if (descContent != null) {
            description = descContent.text();
        } else {
            description = text(doc, ".listing-detail-description, .listing-detail__description");
        }

        BigDecimal areaM2 = null;
        BigDecimal rooms = null;
        BigDecimal bedrooms = null;
        BigDecimal bathrooms = null;
        LocalDate availableFrom = null;
        Integer minimumLeaseMonths = null;
        String furnishingTypeCode = null;
        String propertyTypeCode = null;
        String postalCode = null;
        String houseNumber = null;
        String energyLabel = null;
        BigDecimal deposit = null;
        String displayDeposit = null;
        Double lat = null;
        Double lon = null;

        String summaryLocation = text(doc, ".listing-detail-summary__location");
        if (summaryLocation != null) {
            Matcher m = POSTCODE_PATTERN.matcher(summaryLocation);
            if (m.find()) {
                postalCode = m.group(1).trim();
            }
        }

        Element mapEl = doc.selectFirst("wc-detail-map[data-latitude][data-longitude]");
        if (mapEl != null) {
            try {
                String latStr = mapEl.attr("data-latitude");
                String lonStr = mapEl.attr("data-longitude");
                if (latStr != null && !latStr.isBlank()) {
                    lat = Double.parseDouble(latStr.trim());
                }
                if (lonStr != null && !lonStr.isBlank()) {
                    lon = Double.parseDouble(lonStr.trim());
                }
            } catch (RuntimeException ex) {
                log.warn("Failed to parse lat/lon from detail map on {}", url, ex);
            }
        }

        for (Element dl : doc.select(".listing-features__list")) {
            Elements terms = dl.select("dt.listing-features__term");
            for (Element term : terms) {
                String label = term.text();
                Element dd = term.nextElementSibling();
                if (dd == null) {
                    continue;
                }

                Element mainDesc = dd.selectFirst(".listing-features__main-description");
                String value = mainDesc != null ? mainDesc.text() : dd.text();
                if (label == null || value == null) {
                    continue;
                }

                String lname = label.toLowerCase().trim();
                try {
                    if (lname.contains("living area")) {
                        areaM2 = parseNumber(value);
                    } else if (lname.contains("number of rooms")) {
                        rooms = parseNumber(value);
                    } else if (lname.contains("number of bedrooms")) {
                        bedrooms = parseNumber(value);
                    } else if (lname.contains("number of bathrooms")) {
                        bathrooms = parseNumber(value);
                    } else if (lname.contains("available")) {
                        availableFrom = parseAvailableDate(value);
                    } else if (lname.contains("energy rating")) {
                        energyLabel = value.trim();
                    } else if (lname.contains("type of house")) {
                        propertyTypeCode = normalizePropertyType(value);
                    } else if (lname.contains("interior") || lname.contains("furnishing")) {
                        furnishingTypeCode = normalizeFurnishing(value);
                    } else if (lname.contains("postal code")) {
                        postalCode = value.trim();
                    } else if (lname.contains("house number")) {
                        houseNumber = value.trim();
                    } else if (lname.contains("deposit")) {
                        deposit = parseNumber(value);
                        displayDeposit = value.trim();
                    } else if (lname.contains("duration")) {
                        minimumLeaseMonths = parseDurationMonths(value);
                    }
                } catch (RuntimeException ex) {
                    log.warn("Failed to parse feature '{}' with value '{}' on detail page {}",
                            label, value, url, ex);
                }
            }
        }

        List<String> photos = new ArrayList<>();

        Element carousel = doc.selectFirst("wc-carrousel.carrousel--listing-detail");
        if (carousel != null) {
            for (Element img : carousel.select("img.picture__image")) {
                String src = img.absUrl("src");
                if (src.startsWith("http") && !photos.contains(src)) {
                    photos.add(src);
                }
            }

            for (Element template : carousel.select("wc-picture > template")) {
                for (Element img : template.select("img.picture__image")) {
                    String src = img.absUrl("src");
                    if (src.startsWith("http") && !photos.contains(src)) {
                        photos.add(src);
                    }
                }
            }
        }

        if (photos.isEmpty()) {
            for (Element img : doc.select(".gallery img, .listing-detail__gallery img")) {
                String src = img.absUrl("src");
                if (!src.isBlank() && src.startsWith("http") && !photos.contains(src)) {
                    photos.add(src);
                }
            }
        }

        log.debug(
                "Parsed detail page: url={}, areaM2={}, rooms={}, bedrooms={}, bathrooms={}, energyLabel={}, " +
                        "deposit={}, durationMonths={}, photos={}",
                url, areaM2, rooms, bedrooms, bathrooms, energyLabel, deposit, minimumLeaseMonths, photos.size()
        );

        return new Detail(
                description,
                areaM2,
                rooms,
                bedrooms,
                bathrooms,
                availableFrom,
                minimumLeaseMonths,
                furnishingTypeCode,
                propertyTypeCode,
                postalCode,
                houseNumber,
                energyLabel,
                deposit,
                displayDeposit,
                lat,
                lon,
                photos
        );
    }

    private BigDecimal parseNumber(String text) {
        if (text == null) {
            return null;
        }
        String digits = text.replaceAll("[^\\d.]", "");
        if (digits.isEmpty()) {
            log.debug("Failed to parse number from text='{}'", text);
            return null;
        }
        try {
            return new BigDecimal(digits);
        } catch (NumberFormatException ex) {
            log.warn("NumberFormatException while parsing number from digits='{}' (original='{}')", digits, text, ex);
            return null;
        }
    }

    private Integer parseDurationMonths(String value) {
        if (value == null) {
            return null;
        }
        Matcher m = MONTHS_PATTERN.matcher(value);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ex) {
                log.debug("Could not parse duration months from value='{}'", value, ex);
            }
        }
        return null;
    }

    private LocalDate parseAvailableDate(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        String lower = trimmed.toLowerCase();
        if ("immediately".equals(lower)) {
            return LocalDate.now();
        }

        for (DateTimeFormatter formatter : new DateTimeFormatter[]{FORMAT_DD_MM_YYYY, FORMAT_D_MMMM_YYYY}) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ex) {
                log.trace("Failed to parse available date '{}' with formatter {}", value, formatter);
            }
        }

        log.debug("Could not parse available date '{}'", value);
        return null;
    }

    private String normalizeFurnishing(String value) {
        String v = value.toLowerCase();
        if (v.contains("upholstered")) {
            return "SEMI_FURNISHED";   // Pararius term
        }
        if (v.contains("semi")) {
            return "SEMI_FURNISHED";
        }
        if (v.contains("furnished")) {
            return "FURNISHED";
        }
        if (v.contains("unfurnished")) {
            return "UNFURNISHED";
        }
        return null;
    }

    private String normalizePropertyType(String value) {
        String v = value.toLowerCase();
        if (v.contains("apartment")) {
            return "APARTMENT";
        }
        if (v.contains("flat")) {
            return "APARTMENT";
        }
        if (v.contains("upper floor")) {
            return "APARTMENT";
        }
        if (v.contains("house")) {
            return "HOUSE";
        }
        if (v.contains("studio")) {
            return "STUDIO";
        }
        if (v.contains("room")) {
            return "ROOM";
        }
        return null;
    }

    private record Detail(
            String description,
            BigDecimal areaM2,
            BigDecimal rooms,
            BigDecimal bedrooms,
            BigDecimal bathrooms,
            LocalDate availableFrom,
            Integer minimumLeaseMonths,
            String furnishingTypeCode,
            String propertyTypeCode,
            String postalCode,
            String houseNumber,
            String energyLabel,
            BigDecimal deposit,
            String displayDeposit,
            Double lat,
            Double lon,
            List<String> photoUrls
    ) {}
}
