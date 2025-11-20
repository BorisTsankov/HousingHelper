package nl.fontys.s3.back_end.mapper;

import nl.fontys.s3.back_end.dto.ListingDto;
import nl.fontys.s3.back_end.entity.Listing;
import nl.fontys.s3.back_end.model.ListingModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class ListingMapperTest {


    @Test
    void toModel_returnsNullWhenEntityNull() {
        assertThat(ListingMapper.toModel(null)).isNull();
    }

    @Test
    void toModel_mapsSimpleFieldsCorrectly() {
        Listing entity = new Listing();
        entity.setId(10L);
        entity.setTitle("Nice Apartment");
        entity.setPrimaryPhotoUrl("photo.jpg");

        entity.setRentAmount(new BigDecimal("1200.00"));
        entity.setDeposit(new BigDecimal("2400.00"));

        entity.setDescription("Great place.");
        entity.setAreaM2(new BigDecimal("65.5"));
        entity.setRooms(new BigDecimal("3.0"));
        entity.setBedrooms(new BigDecimal("2.0"));
        entity.setBathrooms(new BigDecimal("1.0"));

        entity.setAvailableFrom(LocalDate.of(2024, 5, 1));
        entity.setAvailableUntil(LocalDate.of(2024, 10, 1));
        entity.setMinimumLeaseMonths(12);

        entity.setCountry("NL");
        entity.setCity("Eindhoven");
        entity.setPostalCode("5611AB");
        entity.setStreet("Main Street");
        entity.setHouseNumber("12");
        entity.setUnit("A");

        entity.setLat(51.4416);
        entity.setLon(5.4697);

        entity.setPhotosCount(8);
        entity.setCanonicalUrl("http://example.com");
        entity.setExternalId("ABC123");

        ListingModel model = ListingMapper.toModel(entity);

        assertThat(model.getId()).isEqualTo(10L);
        assertThat(model.getTitle()).isEqualTo("Nice Apartment");
        assertThat(model.getPrimaryPhotoUrl()).isEqualTo("photo.jpg");

        assertThat(model.getRentAmount()).isEqualByComparingTo("1200.00");
        assertThat(model.getDeposit()).isEqualByComparingTo("2400.00");

        assertThat(model.getDescription()).isEqualTo("Great place.");
        assertThat(model.getAreaM2()).isEqualByComparingTo("65.5");
        assertThat(model.getRooms()).isEqualByComparingTo("3.0");
        assertThat(model.getBedrooms()).isEqualByComparingTo("2.0");
        assertThat(model.getBathrooms()).isEqualByComparingTo("1.0");

        assertThat(model.getAvailableFrom()).isEqualTo(LocalDate.of(2024, 5, 1));
        assertThat(model.getAvailableUntil()).isEqualTo(LocalDate.of(2024, 10, 1));
        assertThat(model.getMinimumLeaseMonths()).isEqualTo(12);

        assertThat(model.getCountry()).isEqualTo("NL");
        assertThat(model.getCity()).isEqualTo("Eindhoven");
        assertThat(model.getPostalCode()).isEqualTo("5611AB");
        assertThat(model.getStreet()).isEqualTo("Main Street");
        assertThat(model.getHouseNumber()).isEqualTo("12");
        assertThat(model.getUnit()).isEqualTo("A");

        assertThat(model.getLat()).isEqualTo(51.4416);
        assertThat(model.getLon()).isEqualTo(5.4697);

        assertThat(model.getPhotosCount()).isEqualTo(8);
        assertThat(model.getCanonicalUrl()).isEqualTo("http://example.com");
        assertThat(model.getExternalId()).isEqualTo("ABC123");

        assertThat(model.getStatus()).isNull();
        assertThat(model.getPropertyType()).isNull();
        assertThat(model.getFurnishingType()).isNull();
        assertThat(model.getRentPeriod()).isNull();
        assertThat(model.getSourceName()).isNull();
    }


    @Test
    void toListingDto_returnsNullWhenModelNull() {
        assertThat(ListingMapper.toListingDto(null)).isNull();
    }

    @Test
    void toListingDto_mapsBasicFieldsCorrectly() {
        ListingModel m = new ListingModel();
        m.setId(15L);
        m.setTitle("Cool House");
        m.setPrimaryPhotoUrl("img.png");
        m.setRentAmount(new BigDecimal("1500.00"));
        m.setDeposit(new BigDecimal("3000.00"));
        m.setCity("Rotterdam");
        m.setCountry("NL");

        ListingDto dto = ListingMapper.toListingDto(m);

        // id is mapped as String
        assertThat(dto.id()).isEqualTo("15");
        assertThat(dto.title()).isEqualTo("Cool House");
        assertThat(dto.image()).isEqualTo("img.png");
        assertThat(dto.location()).isEqualTo("Rotterdam");

        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        String expectedRent = nf.format(new BigDecimal("1500.00"));
        String expectedDeposit = nf.format(new BigDecimal("3000.00"));

        assertThat(dto.displayPrice()).isEqualTo(expectedRent + "/mo");
        assertThat(dto.displayDeposit()).isEqualTo(expectedDeposit);

        assertThat(dto.rentAmount()).isEqualByComparingTo("1500.00");
        assertThat(dto.deposit()).isEqualByComparingTo("3000.00");
    }

    @Test
    void toListingDto_defaultTitleIfMissingOrBlank() {
        ListingModel m1 = new ListingModel();
        m1.setId(5L);
        m1.setTitle(null);

        ListingDto dto1 = ListingMapper.toListingDto(m1);
        assertThat(dto1.title()).isEqualTo("Listing#5");

        ListingModel m2 = new ListingModel();
        m2.setId(6L);
        m2.setTitle("   ");

        ListingDto dto2 = ListingMapper.toListingDto(m2);
        assertThat(dto2.title()).isEqualTo("Listing#6");
    }

    @Test
    void toListingDto_defaultImageIfMissingOrBlank() {
        ListingModel m1 = new ListingModel();
        m1.setId(1L);
        m1.setPrimaryPhotoUrl(null);

        ListingDto dto1 = ListingMapper.toListingDto(m1);
        assertThat(dto1.image()).isEqualTo("https://via.placeholder.com/400x250?text=No+Image");

        ListingModel m2 = new ListingModel();
        m2.setId(2L);
        m2.setPrimaryPhotoUrl("  ");

        ListingDto dto2 = ListingMapper.toListingDto(m2);
        assertThat(dto2.image()).isEqualTo("https://via.placeholder.com/400x250?text=No+Image");
    }

    @Test
    void toListingDto_locationFallback_logic() {
        ListingModel m = new ListingModel();
        m.setId(1L);

        m.setCity("Berlin");
        m.setCountry("Germany");
        ListingDto dto1 = ListingMapper.toListingDto(m);
        assertThat(dto1.location()).isEqualTo("Berlin");

        m.setCity(null);
        m.setCountry("Germany");
        ListingDto dto2 = ListingMapper.toListingDto(m);
        assertThat(dto2.location()).isEqualTo("Germany");

        m.setCity(null);
        m.setCountry(null);
        ListingDto dto3 = ListingMapper.toListingDto(m);
        assertThat(dto3.location()).isEqualTo("Unknown");
    }

    @Test
    void toListingDto_priceOnRequestWhenRentIsNull() {
        ListingModel m = new ListingModel();
        m.setId(1L);
        m.setRentAmount(null);

        ListingDto dto = ListingMapper.toListingDto(m);

        assertThat(dto.displayPrice()).isEqualTo("Price on request");
        assertThat(dto.rentAmount()).isNull();
    }

    @Test
    void toListingDto_nullDepositHandledCorrectly() {
        ListingModel m = new ListingModel();
        m.setId(1L);
        m.setDeposit(null);

        ListingDto dto = ListingMapper.toListingDto(m);

        assertThat(dto.displayDeposit()).isNull();
        assertThat(dto.deposit()).isNull();
    }
}
