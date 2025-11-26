package nl.fontys.s3.back_end.scraper.pararius;

import nl.fontys.s3.back_end.dto.ListingDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParariusScraperUsingListingDto {

    private static final Logger log = LoggerFactory.getLogger(ParariusScraperUsingListingDto.class);

    private static final String BASE_URL = "https://www.pararius.com";

    /**
     * Scrapes Pararius apartments for a given city (slug) and returns ListingDto objects.
     * Example slug: "eindhoven"
     */
    public List<ListingDto> scrapeCity(String citySlug, int maxPages) throws IOException {
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
            } catch (IOException e) {
                log.error("Failed to fetch Pararius page {} for city='{}' (url={})", page, citySlug, url, e);
                // rethrow so caller can decide what to do
                throw e;
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
                } catch (Exception ex) {
                    // Catching generic exception here so one broken listing doesn't kill the whole page
                    log.error("Error while parsing a listing item on page {} city='{}'", page, citySlug, ex);
                }
            }
        }

        log.info("Finished Pararius scrape for city='{}'. Total listings scraped: {}", citySlug, result.size());
        return result;
    }

    private ListingDto parseItemToDto(Element item) throws IOException {
        Element linkEl = item.selectFirst(".listing-search-item__link, a[href]");
        if (linkEl == null) {
            log.warn("Listing item without a link element encountered. Skipping.");
            return null;
        }

        String href = linkEl.attr("href");
        if (href == null || href.isBlank()) {
            log.warn("Listing item has empty href attribute. Skipping.");
            return null;
        }

        String fullUrl = href.startsWith("http") ? href : BASE_URL + href;

        // Use URL path as externalId (stable enough)
        String externalId = href.replaceFirst("^/+", "").replaceAll("/$", "");

        String title = text(item, ".listing-search-item__title");
        if (title == null || title.isBlank()) {
            log.debug("Listing item has no title for externalId='{}'.", externalId);
        }

        String priceText = text(item, ".listing-search-item__price");
        BigDecimal rentAmount = parsePrice(priceText);

        String locationText = text(item, ".listing-search-item__location");
        String city = null;
        String street = null;
        if (locationText != null) {
            String[] parts = locationText.split(",");
            if (parts.length >= 1) street = parts[0].trim();
            if (parts.length >= 2) city = parts[1].trim();
        }

        String imageUrl = null;
        Element imgEl = item.selectFirst("img");
        if (imgEl != null) {
            imageUrl = imgEl.absUrl("src");
        }

        Detail detail;
        try {
            detail = fetchDetailPage(fullUrl);
        } catch (IOException e) {
            // Log and rethrow so you see which listing failed
            log.error("Failed to fetch detail page for listing externalId='{}', url={}", externalId, fullUrl, e);
            throw e;
        }

        List<String> photos = !detail.photoUrls.isEmpty()
                ? detail.photoUrls
                : (imageUrl != null ? List.of(imageUrl) : List.of());

        // Build your existing ListingDto
        return new ListingDto(
                null,                // id (DB will generate)
                title,
                imageUrl,
                priceText,           // price (display)
                locationText,        // location (display)

                priceText,           // displayPrice
                null,                // displayDeposit

                rentAmount,
                null,                // rentPeriod (string; mapped in DB if you want)
                null,                // deposit

                detail.description,
                "ACTIVE",            // status (string label)
                detail.propertyTypeCode,
                detail.furnishingTypeCode,
                null,                // energyLabel

                detail.areaM2,
                null,                // rooms
                detail.bedrooms,
                detail.bathrooms,

                detail.availableFrom,
                null,                // availableUntil
                null,                // minimumLeaseMonths

                "NL",
                city,
                detail.postalCode,
                street,
                detail.houseNumber,
                null,                // unit

                null,                // lat
                null,                // lon

                photos.isEmpty() ? 0 : photos.size(),
                fullUrl,

                externalId,
                "PARARIUS",          // source (label-ish)

                photos
        );
    }

    private String text(Element root, String selector) {
        Element el = root.selectFirst(selector);
        if (el == null) {
            log.trace("Selector '{}' not found in element.", selector);
        }
        return el != null ? el.text() : null;
    }

    private BigDecimal parsePrice(String text) {
        if (text == null) return null;
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

    private Detail fetchDetailPage(String url) throws IOException {
        log.debug("Fetching Pararius detail page: {}", url);

        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; ParariusScraper/1.0)")
                    .timeout(15000)
                    .get();
        } catch (IOException e) {
            log.error("Failed to load detail page: {}", url, e);
            throw e;
        }

        String description = text(doc, ".listing-detail-description, .listing-detail__description");

        BigDecimal areaM2 = null;
        BigDecimal bedrooms = null;
        BigDecimal bathrooms = null;
        LocalDate availableFrom = null;
        String furnishingTypeCode = null;
        String propertyTypeCode = null;
        String postalCode = null;
        String houseNumber = null;

        for (Element row : doc.select(".listing-features__item")) {
            String label = text(row, ".listing-features__title");
            String value = text(row, ".listing-features__value");
            if (label == null || value == null) continue;

            String lname = label.toLowerCase();
            try {
                if (lname.contains("living area")) {
                    areaM2 = parseNumber(value);
                } else if (lname.contains("bedrooms")) {
                    bedrooms = parseNumber(value);
                } else if (lname.contains("bathrooms")) {
                    bathrooms = parseNumber(value);
                } else if (lname.contains("available from")) {
                    // If you want real dates, parse them here
                    availableFrom = null;
                } else if (lname.contains("furnishing")) {
                    furnishingTypeCode = normalizeFurnishing(value);
                } else if (lname.contains("property type")) {
                    propertyTypeCode = normalizePropertyType(value);
                } else if (lname.contains("postal code")) {
                    postalCode = value.trim();
                } else if (lname.contains("house number")) {
                    houseNumber = value.trim();
                }
            } catch (Exception ex) {
                log.warn("Failed to parse feature '{}' with value '{}' on detail page {}", label, value, url, ex);
            }
        }

        List<String> photos = new ArrayList<>();
        for (Element img : doc.select(".gallery img, .listing-detail__gallery img")) {
            String src = img.absUrl("src");
            if (!src.isBlank()) photos.add(src);
        }

        log.debug("Parsed detail page: url={}, areaM2={}, bedrooms={}, bathrooms={}, photos={}",
                url, areaM2, bedrooms, bathrooms, photos.size());

        return new Detail(
                description,
                areaM2,
                bedrooms,
                bathrooms,
                availableFrom,
                furnishingTypeCode,
                propertyTypeCode,
                postalCode,
                houseNumber,
                photos
        );
    }

    private BigDecimal parseNumber(String text) {
        if (text == null) return null;
        String digits = text.replaceAll("[^0-9.]", "");
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

    private String normalizeFurnishing(String value) {
        String v = value.toLowerCase();
        if (v.contains("semi")) return "SEMI_FURNISHED";
        if (v.contains("furnished")) return "FURNISHED";
        if (v.contains("unfurnished")) return "UNFURNISHED";
        return null;
    }

    private String normalizePropertyType(String value) {
        String v = value.toLowerCase();
        if (v.contains("apartment")) return "APARTMENT";
        if (v.contains("house")) return "HOUSE";
        if (v.contains("studio")) return "STUDIO";
        if (v.contains("room")) return "ROOM";
        return null;
    }

    private record Detail(
            String description,
            BigDecimal areaM2,
            BigDecimal bedrooms,
            BigDecimal bathrooms,
            LocalDate availableFrom,
            String furnishingTypeCode,
            String propertyTypeCode,
            String postalCode,
            String houseNumber,
            List<String> photoUrls
    ) {}
}
