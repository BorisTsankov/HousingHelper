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
            String url = buildSearchUrl(citySlug, page);
            Document doc = fetchSearchPage(citySlug, page, url);

            if (doc == null) {
                log.warn("Search page document is null for city='{}', page={}. Stopping pagination.", citySlug, page);
                break;
            }

            Elements items = doc.select(".listing-search-item");
            if (noItemsOnPage(items, citySlug, page)) {
                break;
            }

            log.debug("Found {} listing elements on Pararius page {} for city='{}'",
                    items.size(), page, citySlug);

            processPageItems(items, citySlug, page, url, result);
        }

        log.info("Finished Pararius scrape for city='{}'. Total listings scraped: {}", citySlug, result.size());
        return result;
    }

    private String buildSearchUrl(String citySlug, int page) {
        return BASE_URL + "/apartments/" + citySlug + "/page-" + page;
    }

    /**
     * Fetch search page.
     * On IO errors, log and return an empty document so we can stop gracefully.
     */
    private Document fetchSearchPage(String citySlug, int page, String url) {
        try {
            log.debug("Fetching Pararius search page {} for city='{}' -> {}", page, citySlug, url);
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; ParariusScraper/1.0)")
                    .timeout(15000)
                    .get();
        } catch (java.io.IOException e) {
            log.error("Failed to fetch Pararius page {} for city='{}' (url={}). " +
                            "Returning empty document and stopping pagination.",
                    page, citySlug, url, e);
            // Return an empty document so noItemsOnPage() will stop the loop
            return Jsoup.parse("<html></html>");
        }
    }

    private boolean noItemsOnPage(Elements items, String citySlug, int page) {
        if (items.isEmpty()) {
            log.warn("No listing items found on Pararius page {} for city='{}'. Stopping pagination.", page, citySlug);
            return true;
        }
        return false;
    }

    private void processPageItems(Elements items,
                                  String citySlug,
                                  int page,
                                  String url,
                                  List<ListingDto> result) {
        for (Element item : items) {
            parseAndAddItem(item, citySlug, page, url, result);
        }
    }

    private void parseAndAddItem(Element item,
                                 String citySlug,
                                 int page,
                                 String url,
                                 List<ListingDto> result) {
        try {
            ListingDto dto = parseItemToDto(item);
            if (dto != null) {
                result.add(dto);
            } else {
                log.warn("parseItemToDto returned null for an item on page {} city='{}' (searchUrl={})",
                        page, citySlug, url);
            }
        } catch (RuntimeException ex) {
            // Log and skip this item; do not fail the whole scrape
            log.error("Unexpected error while parsing a listing item on page {} city='{}' (searchUrl={}). " +
                            "Skipping this item.",
                    page, citySlug, url, ex);
        }
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
        String digits = text.replaceAll("\\D", "");
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

    /**
     * Fetch detail page and parse detail data.
     * On any error, log and return an "empty" Detail with mostly nulls.
     */
    private Detail fetchDetailPage(String url) {
        log.debug("Fetching Pararius detail page: {}", url);

        try {
            Document doc = loadDetailDocument(url);
            if (doc == null) {
                log.warn("Detail document is null for url={}. Returning empty Detail.", url);
                return emptyDetail();
            }

            String description = extractDescription(doc);

            String postalCodeFromSummary = extractPostalCodeFromSummary(doc);
            LatLon latLon = extractLatLon(doc, url);
            FeatureData featureData = extractFeatureData(doc, url, postalCodeFromSummary);
            List<String> photos = extractPhotos(doc);

            log.debug(
                    "Parsed detail page: url={}, areaM2={}, rooms={}, bedrooms={}, bathrooms={}, energyLabel={}, " +
                            "deposit={}, durationMonths={}, photos={}",
                    url,
                    featureData.areaM2(),
                    featureData.rooms(),
                    featureData.bedrooms(),
                    featureData.bathrooms(),
                    featureData.energyLabel(),
                    featureData.deposit(),
                    featureData.minimumLeaseMonths(),
                    photos.size()
            );

            return new Detail(
                    description,
                    featureData.areaM2(),
                    featureData.rooms(),
                    featureData.bedrooms(),
                    featureData.bathrooms(),
                    featureData.availableFrom(),
                    featureData.minimumLeaseMonths(),
                    featureData.furnishingTypeCode(),
                    featureData.propertyTypeCode(),
                    featureData.postalCode(),
                    featureData.houseNumber(),
                    featureData.energyLabel(),
                    featureData.deposit(),
                    featureData.displayDeposit(),
                    latLon.lat(),
                    latLon.lon(),
                    photos
            );
        } catch (RuntimeException ex) {
            log.error("Unexpected error while parsing Pararius detail page: {}. " +
                            "Returning empty Detail.",
                    url, ex);
            return emptyDetail();
        }
    }

    private Detail emptyDetail() {
        return new Detail(
                null, // description
                null, // areaM2
                null, // rooms
                null, // bedrooms
                null, // bathrooms
                null, // availableFrom
                null, // minimumLeaseMonths
                null, // furnishingTypeCode
                null, // propertyTypeCode
                null, // postalCode
                null, // houseNumber
                null, // energyLabel
                null, // deposit
                null, // displayDeposit
                null, // lat
                null, // lon
                List.of() // photoUrls
                // or Collections.emptyList()
        );
    }

    private Document loadDetailDocument(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; ParariusScraper/1.0)")
                    .timeout(15000)
                    .get();
        } catch (java.io.IOException e) {
            log.error("Failed to load detail page: {}. Returning null document.", url, e);
            return null;
        }
    }

    private String extractDescription(Document doc) {
        Element descContent = doc.selectFirst(".listing-detail-description__content");
        if (descContent != null) {
            return descContent.text();
        }
        return text(doc, ".listing-detail-description, .listing-detail__description");
    }

    private String extractPostalCodeFromSummary(Document doc) {
        String summaryLocation = text(doc, ".listing-detail-summary__location");
        if (summaryLocation == null) {
            return null;
        }

        Matcher m = POSTCODE_PATTERN.matcher(summaryLocation);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }

    private LatLon extractLatLon(Document doc, String url) {
        Double lat = null;
        Double lon = null;

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

        return new LatLon(lat, lon);
    }

    private record LatLon(Double lat, Double lon) {}

    private FeatureData extractFeatureData(Document doc, String url, String initialPostalCode) {
        FeatureData data = new FeatureData();
        data.setPostalCode(initialPostalCode);

        for (Element dl : doc.select(".listing-features__list")) {
            Elements terms = dl.select("dt.listing-features__term");
            for (Element term : terms) {
                handleFeatureTerm(term, data, url);
            }
        }

        return data;
    }

    private void handleFeatureTerm(Element term, FeatureData data, String url) {
        String label = term.text();
        Element dd = term.nextElementSibling();

        if (dd == null || label == null) {
            return;
        }

        Element mainDesc = dd.selectFirst(".listing-features__main-description");
        String value = mainDesc != null ? mainDesc.text() : dd.text();

        if (value == null) {
            return;
        }

        String lname = label.toLowerCase().trim();
        try {
            applyFeature(lname, value, data);
        } catch (RuntimeException ex) {
            log.warn("Failed to parse feature '{}' with value '{}' on detail page {}",
                    label, value, url, ex);
        }
    }

    private void applyFeature(String lname, String value, FeatureData data) {
        if (lname.contains("living area")) {
            data.setAreaM2(parseNumber(value));
        } else if (lname.contains("number of rooms")) {
            data.setRooms(parseNumber(value));
        } else if (lname.contains("number of bedrooms")) {
            data.setBedrooms(parseNumber(value));
        } else if (lname.contains("number of bathrooms")) {
            data.setBathrooms(parseNumber(value));
        } else if (lname.contains("available")) {
            data.setAvailableFrom(parseAvailableDate(value));
        } else if (lname.contains("energy rating")) {
            data.setEnergyLabel(value.trim());
        } else if (lname.contains("type of house")) {
            data.setPropertyTypeCode(normalizePropertyType(value));
        } else if (lname.contains("interior") || lname.contains("furnishing")) {
            data.setFurnishingTypeCode(normalizeFurnishing(value));
        } else if (lname.contains("postal code")) {
            data.setPostalCode(value.trim());
        } else if (lname.contains("house number")) {
            data.setHouseNumber(value.trim());
        } else if (lname.contains("deposit")) {
            data.setDeposit(parseNumber(value));
            data.setDisplayDeposit(value.trim());
        } else if (lname.contains("duration")) {
            data.setMinimumLeaseMonths(parseDurationMonths(value));
        }
    }

    private static final class FeatureData {
        private BigDecimal areaM2;
        private BigDecimal rooms;
        private BigDecimal bedrooms;
        private BigDecimal bathrooms;
        private LocalDate availableFrom;
        private Integer minimumLeaseMonths;
        private String furnishingTypeCode;
        private String propertyTypeCode;
        private String postalCode;
        private String houseNumber;
        private String energyLabel;
        private BigDecimal deposit;
        private String displayDeposit;

        public BigDecimal areaM2() { return areaM2; }
        public BigDecimal rooms() { return rooms; }
        public BigDecimal bedrooms() { return bedrooms; }
        public BigDecimal bathrooms() { return bathrooms; }
        public LocalDate availableFrom() { return availableFrom; }
        public Integer minimumLeaseMonths() { return minimumLeaseMonths; }
        public String furnishingTypeCode() { return furnishingTypeCode; }
        public String propertyTypeCode() { return propertyTypeCode; }
        public String postalCode() { return postalCode; }
        public String houseNumber() { return houseNumber; }
        public String energyLabel() { return energyLabel; }
        public BigDecimal deposit() { return deposit; }
        public String displayDeposit() { return displayDeposit; }

        public void setAreaM2(BigDecimal v) { this.areaM2 = v; }
        public void setRooms(BigDecimal v) { this.rooms = v; }
        public void setBedrooms(BigDecimal v) { this.bedrooms = v; }
        public void setBathrooms(BigDecimal v) { this.bathrooms = v; }
        public void setAvailableFrom(LocalDate v) { this.availableFrom = v; }
        public void setMinimumLeaseMonths(Integer v) { this.minimumLeaseMonths = v; }
        public void setFurnishingTypeCode(String v) { this.furnishingTypeCode = v; }
        public void setPropertyTypeCode(String v) { this.propertyTypeCode = v; }
        public void setPostalCode(String v) { this.postalCode = v; }
        public void setHouseNumber(String v) { this.houseNumber = v; }
        public void setEnergyLabel(String v) { this.energyLabel = v; }
        public void setDeposit(BigDecimal v) { this.deposit = v; }
        public void setDisplayDeposit(String v) { this.displayDeposit = v; }
    }

    private List<String> extractPhotos(Document doc) {
        List<String> photos = new ArrayList<>();

        Element carousel = doc.selectFirst("wc-carrousel.carrousel--listing-detail");
        if (carousel != null) {
            for (Element img : carousel.select("img.picture__image")) {
                addPhotoIfValid(photos, img.absUrl("src"));
            }

            for (Element template : carousel.select("wc-picture > template")) {
                for (Element img : template.select("img.picture__image")) {
                    addPhotoIfValid(photos, img.absUrl("src"));
                }
            }
        }

        if (photos.isEmpty()) {
            for (Element img : doc.select(".gallery img, .listing-detail__gallery img")) {
                addPhotoIfValid(photos, img.absUrl("src"));
            }
        }

        return photos;
    }

    private void addPhotoIfValid(List<String> photos, String src) {
        if (src != null && !src.isBlank() && src.startsWith("http") && !photos.contains(src)) {
            photos.add(src);
        }
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
        String apartment = "APARTMENT";
        if (v.contains("apartment")) {
            return apartment;
        }
        if (v.contains("flat")) {
            return apartment;
        }
        if (v.contains("upper floor")) {
            return apartment;
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
