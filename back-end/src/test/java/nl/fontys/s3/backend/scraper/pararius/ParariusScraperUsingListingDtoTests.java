package nl.fontys.s3.backend.scraper.pararius;


import nl.fontys.s3.backend.dto.ListingDto;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParariusScraperUsingListingDtoTests {

    private final ParariusScraperUsingListingDto scraper = new ParariusScraperUsingListingDto();

    // ---------- scrapeCity + Jsoup static mocking ----------

    @Test
    void scrapeCity_stopsWhenNoItemsOnFirstPage() throws Exception {
        String citySlug = "eindhoven";
        int maxPages = 5;
        String searchUrl1 = "https://www.pararius.com/apartments/" + citySlug + "/page-" + 1;

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            Connection searchConn = mock(Connection.class);
            Document emptyDoc = Jsoup.parse("<html><body></body></html>");

            jsoupMock.when(() -> Jsoup.connect(searchUrl1)).thenReturn(searchConn);

            when(searchConn.userAgent(anyString())).thenReturn(searchConn);
            when(searchConn.timeout(anyInt())).thenReturn(searchConn);
            when(searchConn.get()).thenReturn(emptyDoc);

            List<ListingDto> results = scraper.scrapeCity(citySlug, maxPages);

            assertThat(results).isEmpty();
        }
    }

    @Test
    void scrapeCity_parsesSingleListingWithDetailPage() throws Exception {
        String citySlug = "eindhoven";
        int maxPages = 1;
        String searchUrl1 = "https://www.pararius.com/apartments/" + citySlug + "/page-1";

        // href in listing
        String relativeHref = "/apartment-for-rent/eindhoven/test-slug";
        String detailUrl = "https://www.pararius.com" + relativeHref;

        String searchHtml = """
                <html><body>
                  <div class="listing-search-item">
                    <a class="listing-search-item__link" href="%s">View</a>
                    <span class="listing-search-item__title">Nice apartment</span>
                    <span class="listing-search-item__price">€ 1,234 per month</span>
                    <span class="listing-search-item__location">Main Street, Eindhoven</span>
                    <img src="https://img/listing-main.jpg" />
                  </div>
                </body></html>
                """.formatted(relativeHref);

        String detailHtml = """
                <html><body>
                  <div class="listing-detail-description__content">Big and sunny</div>

                  <div class="listing-detail-summary__location">
                    Main Street 12, 5611AB Eindhoven
                  </div>

                  <wc-detail-map data-latitude="51.23" data-longitude="5.12"></wc-detail-map>

                  <dl class="listing-features__list">
                    <dt class="listing-features__term">Living area</dt>
                    <dd><span class="listing-features__main-description">80 m²</span></dd>

                    <dt class="listing-features__term">Number of rooms</dt>
                    <dd><span class="listing-features__main-description">3 rooms</span></dd>

                    <dt class="listing-features__term">Number of bedrooms</dt>
                    <dd><span class="listing-features__main-description">2 bedrooms</span></dd>

                    <dt class="listing-features__term">Number of bathrooms</dt>
                    <dd><span class="listing-features__main-description">1 bathroom</span></dd>

                    <dt class="listing-features__term">Available</dt>
                    <dd><span class="listing-features__main-description">01-12-2025</span></dd>

                    <dt class="listing-features__term">Energy rating</dt>
                    <dd><span class="listing-features__main-description">A</span></dd>

                    <dt class="listing-features__term">Type of house</dt>
                    <dd><span class="listing-features__main-description">Apartment</span></dd>

                    <dt class="listing-features__term">Interior</dt>
                    <dd><span class="listing-features__main-description">Furnished</span></dd>

                    <dt class="listing-features__term">Postal code</dt>
                    <dd><span class="listing-features__main-description">5611 AB</span></dd>

                    <dt class="listing-features__term">House number</dt>
                    <dd><span class="listing-features__main-description">12</span></dd>

                    <dt class="listing-features__term">Deposit</dt>
                    <dd><span class="listing-features__main-description">€ 2,000</span></dd>

                    <dt class="listing-features__term">Duration</dt>
                    <dd><span class="listing-features__main-description">12 months</span></dd>
                  </dl>

                  <wc-carrousel class="carrousel--listing-detail">
                    <img class="picture__image" src="https://img/detail1.jpg"/>
                    <wc-picture>
                      <template>
                        <img class="picture__image" src="https://img/detail2.jpg"/>
                      </template>
                    </wc-picture>
                  </wc-carrousel>
                </body></html>
                """;

        Document searchDoc = Jsoup.parse(searchHtml, searchUrl1);
        Document detailDoc = Jsoup.parse(detailHtml, detailUrl);

        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            Connection searchConn = mock(Connection.class);
            Connection detailConn = mock(Connection.class);

            jsoupMock.when(() -> Jsoup.connect(searchUrl1)).thenReturn(searchConn);
            jsoupMock.when(() -> Jsoup.connect(detailUrl)).thenReturn(detailConn);

            when(searchConn.userAgent(anyString())).thenReturn(searchConn);
            when(searchConn.timeout(anyInt())).thenReturn(searchConn);
            when(searchConn.get()).thenReturn(searchDoc);

            when(detailConn.userAgent(anyString())).thenReturn(detailConn);
            when(detailConn.timeout(anyInt())).thenReturn(detailConn);
            when(detailConn.get()).thenReturn(detailDoc);

            List<ListingDto> results = scraper.scrapeCity(citySlug, maxPages);

            // 1st assertion: list size
            assertThat(results).hasSize(1);

            ListingDto dto = results.get(0);

            // 2nd assertion: fields from list page
            assertThat(dto)
                    .extracting(
                            ListingDto::title,
                            ListingDto::rentAmount,
                            ListingDto::city,
                            ListingDto::street,
                            ListingDto::image,
                            ListingDto::canonicalUrl,
                            ListingDto::externalId
                    )
                    .containsExactly(
                            "Nice apartment",
                            new BigDecimal("1234"),
                            "Eindhoven",
                            "Main Street",
                            "https://img/listing-main.jpg",
                            detailUrl,
                            "apartment-for-rent/eindhoven/test-slug"
                    );

            // 3rd assertion: fields from detail page
            assertThat(dto)
                    .extracting(
                            ListingDto::description,
                            ListingDto::areaM2,
                            ListingDto::rooms,
                            ListingDto::bedrooms,
                            ListingDto::bathrooms,
                            ListingDto::energyLabel,
                            ListingDto::propertyType,
                            ListingDto::furnishingType,
                            ListingDto::deposit,
                            ListingDto::postalCode,
                            ListingDto::houseNumber,
                            ListingDto::country,
                            ListingDto::availableFrom,
                            ListingDto::minimumLeaseMonths,
                            ListingDto::availableUntil
                    )
                    .containsExactly(
                            "Big and sunny",
                            new BigDecimal("80"),
                            new BigDecimal("3"),
                            new BigDecimal("2"),
                            new BigDecimal("1"),
                            "A",
                            "APARTMENT",
                            "FURNISHED",
                            new BigDecimal("2000"),
                            "5611 AB",
                            "12",
                            "NL",
                            LocalDate.of(2025, 12, 1),
                            12,
                            LocalDate.of(2026, 12, 1)
                    );

            // 4th assertion: geo coords
            assertThat(dto)
                    .extracting(ListingDto::lat, ListingDto::lon)
                    .containsExactly(51.23, 5.12);

            // 5th assertion: photos
            assertThat(dto.photoUrls())
                    .containsExactly(
                            "https://img/detail1.jpg",
                            "https://img/detail2.jpg"
                    );

            // 6th assertion: photos count
            assertThat(dto.photosCount()).isEqualTo(2);
        }
    }

    // ---------- helper for calling private methods via reflection ----------

    @SuppressWarnings("unchecked")
    private <T> T invokePrivate(String name, Class<?>[] paramTypes, Object... args) throws Exception {
        Method m = ParariusScraperUsingListingDto.class.getDeclaredMethod(name, paramTypes);
        m.setAccessible(true);
        return (T) m.invoke(scraper, args);
    }

    // ---------- parsePrice / parseNumber / parseDurationMonths ----------

    @Test
    void parsePrice_extractsDigitsAndHandlesInvalid() throws Exception {
        BigDecimal price = invokePrivate(
                "parsePrice",
                new Class[]{String.class},
                "€ 1,234 per month"
        );
        assertThat(price).isEqualByComparingTo(new BigDecimal("1234"));

        BigDecimal nullFromText = invokePrivate(
                "parsePrice",
                new Class[]{String.class},
                "per month"
        );
        assertThat(nullFromText).isNull();

        BigDecimal nullFromNull = invokePrivate(
                "parsePrice",
                new Class[]{String.class},
                (Object) null
        );
        assertThat(nullFromNull).isNull();
    }

    @Test
    void parseNumber_extractsDigitsAndHandlesInvalid() throws Exception {
        BigDecimal number = invokePrivate(
                "parseNumber",
                new Class[]{String.class},
                "80 m²"
        );
        assertThat(number).isEqualByComparingTo(new BigDecimal("80"));

        BigDecimal decimal = invokePrivate(
                "parseNumber",
                new Class[]{String.class},
                "3.5 rooms"
        );
        assertThat(decimal).isEqualByComparingTo(new BigDecimal("3.5"));

        BigDecimal nullFromText = invokePrivate(
                "parseNumber",
                new Class[]{String.class},
                "no digits here"
        );
        assertThat(nullFromText).isNull();

        BigDecimal nullFromNull = invokePrivate(
                "parseNumber",
                new Class[]{String.class},
                (Object) null
        );
        assertThat(nullFromNull).isNull();
    }

    @Test
    void parseDurationMonths_extractsFirstIntegerOrNull() throws Exception {
        Integer months = invokePrivate(
                "parseDurationMonths",
                new Class[]{String.class},
                "12 months"
        );
        assertThat(months).isEqualTo(12);

        Integer noNumber = invokePrivate(
                "parseDurationMonths",
                new Class[]{String.class},
                "unknown"
        );
        assertThat(noNumber).isNull();

        Integer nullInput = invokePrivate(
                "parseDurationMonths",
                new Class[]{String.class},
                (Object) null
        );
        assertThat(nullInput).isNull();
    }

    // ---------- parseAvailableDate ----------

    @Test
    void parseAvailableDate_supportsImmediatelyAndKnownFormats() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate imm = invokePrivate(
                "parseAvailableDate",
                new Class[]{String.class},
                "immediately"
        );
        assertThat(imm).isEqualTo(now);

        LocalDate d1 = invokePrivate(
                "parseAvailableDate",
                new Class[]{String.class},
                "01-12-2025"
        );
        assertThat(d1).isEqualTo(LocalDate.of(2025, 12, 1));

        LocalDate d2 = invokePrivate(
                "parseAvailableDate",
                new Class[]{String.class},
                "1 December 2025"
        );
        assertThat(d2).isEqualTo(LocalDate.of(2025, 12, 1));

        LocalDate unknown = invokePrivate(
                "parseAvailableDate",
                new Class[]{String.class},
                "sometime soon"
        );
        assertThat(unknown).isNull();
    }

    // ---------- normalizeFurnishing / normalizePropertyType ----------

    @Test
    void normalizeFurnishing_mapsToExpectedCodes() throws Exception {
        String upholstered = invokePrivate(
                "normalizeFurnishing",
                new Class[]{String.class},
                "Upholstered"
        );
        assertThat(upholstered).isEqualTo("SEMI_FURNISHED");

        String semi = invokePrivate(
                "normalizeFurnishing",
                new Class[]{String.class},
                "Semi-furnished"
        );
        assertThat(semi).isEqualTo("SEMI_FURNISHED");

        String furnished = invokePrivate(
                "normalizeFurnishing",
                new Class[]{String.class},
                "Furnished"
        );
        assertThat(furnished).isEqualTo("FURNISHED");

        String unfurnished = invokePrivate(
                "normalizeFurnishing",
                new Class[]{String.class},
                "Unfurnished"
        );
        assertThat(unfurnished).isEqualTo("UNFURNISHED");

        String unknown = invokePrivate(
                "normalizeFurnishing",
                new Class[]{String.class},
                "Something else"
        );
        assertThat(unknown).isNull();
    }

    @Test
    void normalizePropertyType_mapsToExpectedCodes() throws Exception {
        String apartment = invokePrivate(
                "normalizePropertyType",
                new Class[]{String.class},
                "Apartment"
        );
        assertThat(apartment).isEqualTo("APARTMENT");

        String flat = invokePrivate(
                "normalizePropertyType",
                new Class[]{String.class},
                "Flat"
        );
        assertThat(flat).isEqualTo("APARTMENT");

        String upperFloor = invokePrivate(
                "normalizePropertyType",
                new Class[]{String.class},
                "Upper floor"
        );
        assertThat(upperFloor).isEqualTo("APARTMENT");

        String house = invokePrivate(
                "normalizePropertyType",
                new Class[]{String.class},
                "Single-family house"
        );
        assertThat(house).isEqualTo("HOUSE");

        String studio = invokePrivate(
                "normalizePropertyType",
                new Class[]{String.class},
                "Studio"
        );
        assertThat(studio).isEqualTo("STUDIO");

        String room = invokePrivate(
                "normalizePropertyType",
                new Class[]{String.class},
                "Room"
        );
        assertThat(room).isEqualTo("ROOM");

        String unknown = invokePrivate(
                "normalizePropertyType",
                new Class[]{String.class},
                "Something else"
        );
        assertThat(unknown).isNull();
    }

    // ---------- buildPhotoList / extractPhotos / parseLocation ----------

    @Test
    void buildPhotoList_prefersDetailPhotosThenFallbackThenEmpty() throws Exception {
        List<String> photosFromDetail = List.of("https://img/1.jpg", "https://img/2.jpg");

        List<String> r1 = invokePrivate(
                "buildPhotoList",
                new Class[]{List.class, String.class},
                photosFromDetail,
                "https://fallback.jpg"
        );
        assertThat(r1).containsExactly("https://img/1.jpg", "https://img/2.jpg");

        List<String> r2 = invokePrivate(
                "buildPhotoList",
                new Class[]{List.class, String.class},
                List.of(),
                "https://fallback.jpg"
        );
        assertThat(r2).containsExactly("https://fallback.jpg");

        List<String> r3 = invokePrivate(
                "buildPhotoList",
                new Class[]{List.class, String.class},
                null,
                null
        );
        assertThat(r3).isEmpty();
    }

    @Test
    void extractPhotos_collectsUniqueHttpImagesFromCarouselAndGallery() throws Exception {
        String html = """
                <html><body>
                  <wc-carrousel class="carrousel--listing-detail">
                    <img class="picture__image" src="https://img/detail1.jpg"/>
                    <img class="picture__image" src="https://img/detail1.jpg"/> <!-- duplicate -->
                    <wc-picture>
                      <template>
                        <img class="picture__image" src="https://img/detail2.jpg"/>
                        <img class="picture__image" src="/relative.jpg"/> <!-- relative, still http? base missing -->
                      </template>
                    </wc-picture>
                  </wc-carrousel>

                  <div class="gallery">
                    <img src="https://img/gallery1.jpg"/>
                    <img src="not-http.jpg"/>
                  </div>
                </body></html>
                """;
        Document doc = Jsoup.parse(html, "https://www.pararius.com/some-detail");

        @SuppressWarnings("unchecked")
        List<String> photos = invokePrivate(
                "extractPhotos",
                new Class[]{Document.class},
                doc
        );

        // We expect only unique, http-prefixed urls
        assertThat(photos).contains(
                "https://img/detail1.jpg",
                "https://img/detail2.jpg"
        );
        // gallery should not be used because carousel already had photos
        assertThat(photos).doesNotContain("https://img/gallery1.jpg");
    }

    @Test
    void parseLocation_splitsStreetAndCity() throws Exception {
        // private record LocationParts; we just call toString or use reflection to get the components
        Object locationParts = invokePrivate(
                "parseLocation",
                new Class[]{String.class},
                "Main Street, Eindhoven"
        );
        Class<?> locationClass = locationParts.getClass();

        Method streetGetter = locationClass.getDeclaredMethod("street");
        Method cityGetter = locationClass.getDeclaredMethod("city");

        String street = (String) streetGetter.invoke(locationParts);
        String city = (String) cityGetter.invoke(locationParts);

        assertThat(street).isEqualTo("Main Street");
        assertThat(city).isEqualTo("Eindhoven");

        Object nullParts = invokePrivate(
                "parseLocation",
                new Class[]{String.class},
                (Object) null
        );
        street = (String) streetGetter.invoke(nullParts);
        city = (String) cityGetter.invoke(nullParts);

        assertThat(street).isNull();
        assertThat(city).isNull();
    }
}
