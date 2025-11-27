package nl.fontys.s3.backend.scraper.pararius;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fontys.s3.backend.dto.ListingDto;
import nl.fontys.s3.backend.entity.*;
import nl.fontys.s3.backend.repository.interfaces.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParariusIngestFromDtoServiceTests {

    @Mock
    private ListingRepository listingRepository;
    @Mock
    private ListingSourceRepository listingSourceRepository;
    @Mock
    private ListingStatusRepository listingStatusRepository;
    @Mock
    private AgencyRepository agencyRepository;
    @Mock
    private ListingPhotoRepository listingPhotoRepository;
    @Mock
    private RawListingRepository rawListingRepository;
    @Mock
    private PropertyTypeRepository propertyTypeRepository;
    @Mock
    private FurnishingTypeRepository furnishingTypeRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ParariusIngestFromDtoService service;

    @Test
    void ingest_throwsWhenListingSourceMissing() {
        ListingDto dto = mock(ListingDto.class);

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ingest(List.of(dto)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ListingSource PARARIUS not found");

        verifyNoInteractions(listingRepository, rawListingRepository);
    }

    @Test
    void ingest_throwsWhenActiveStatusMissing() {
        ListingDto dto = mock(ListingDto.class);
        ListingSource source = new ListingSource();

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.of(source));
        when(listingStatusRepository.findByCode("ACTIVE"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ingest(List.of(dto)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ListingStatus ACTIVE not found");

        verify(listingStatusRepository, never()).findByCode("REMOVED");
        verifyNoInteractions(listingRepository, rawListingRepository);
    }

    @Test
    void ingest_throwsWhenRemovedStatusMissing() {
        ListingDto dto = mock(ListingDto.class);
        ListingSource source = new ListingSource();
        ListingStatus active = new ListingStatus();

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.of(source));
        when(listingStatusRepository.findByCode("ACTIVE"))
                .thenReturn(Optional.of(active));
        when(listingStatusRepository.findByCode("REMOVED"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ingest(List.of(dto)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ListingStatus REMOVED not found");

        verifyNoInteractions(listingRepository, rawListingRepository);
    }

    @Test
    void ingest_happyPath_upsertsListings_andMarksMissingInactive() throws Exception {
        // Arrange
        ListingSource source = new ListingSource();
        ListingStatus active = new ListingStatus();
        ListingStatus removed = new ListingStatus();
        Agency agency = new Agency();
        PropertyType propertyType = new PropertyType();
        FurnishingType furnishingType = new FurnishingType();

        ListingDto dto = mock(ListingDto.class);
        when(dto.externalId()).thenReturn("ext-1");
        when(dto.canonicalUrl()).thenReturn("https://pararius.nl/listing/1");
        when(dto.title()).thenReturn("Nice apartment");
        when(dto.description()).thenReturn("Some description");
        when(dto.propertyType()).thenReturn("APT");
        when(dto.furnishingType()).thenReturn("FURNISHED");
        when(dto.photoUrls()).thenReturn(List.of("https://img1", "https://img2"));

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.of(source));
        when(listingStatusRepository.findByCode("ACTIVE"))
                .thenReturn(Optional.of(active));
        when(listingStatusRepository.findByCode("REMOVED"))
                .thenReturn(Optional.of(removed));
        when(agencyRepository.findByExternalIdAndSource("PARARIUS_AGENCY", source))
                .thenReturn(Optional.of(agency));

        when(propertyTypeRepository.findByCode("APT"))
                .thenReturn(Optional.of(propertyType));
        when(furnishingTypeRepository.findByCode("FURNISHED"))
                .thenReturn(Optional.of(furnishingType));

        when(rawListingRepository.findBySourceAndExternalId(source, "ext-1"))
                .thenReturn(Optional.empty());

        when(listingRepository.findBySourceCodeAndExternalId("PARARIUS", "ext-1"))
                .thenReturn(Optional.empty());

        // Let save(listing) return the same instance for inspection
        when(listingRepository.save(any(Listing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(objectMapper.writeValueAsString(dto))
                .thenReturn("{\"externalId\":\"ext-1\"}");

        when(listingRepository.markMissingListingsInactive(eq(source), eq(active), eq(removed), any(OffsetDateTime.class)))
                .thenReturn(3);

        // Act
        service.ingest(List.of(dto));

        // Assert: listing saved with mapped fields
        ArgumentCaptor<Listing> listingCaptor = ArgumentCaptor.forClass(Listing.class);
        verify(listingRepository, atLeastOnce()).save(listingCaptor.capture());

        Listing saved = listingCaptor.getValue();
        assertThat(saved.getExternalId()).isEqualTo("ext-1");
        assertThat(saved.getSource()).isEqualTo(source);
        assertThat(saved.getStatus()).isEqualTo(active);
        assertThat(saved.getAgency()).isEqualTo(agency);
        assertThat(saved.getTitle()).isEqualTo("Nice apartment");
        assertThat(saved.getDescription()).isEqualTo("Some description");
        assertThat(saved.getPropertyType()).isEqualTo(propertyType);
        assertThat(saved.getFurnishingType()).isEqualTo(furnishingType);
        assertThat(saved.getPrimaryPhotoUrl()).isEqualTo("https://img1");
        assertThat(saved.getPhotosCount()).isEqualTo(2);
        assertThat(saved.getContentHash()).isNotNull();
        assertThat(saved.getFirstSeenAt()).isNotNull();
        assertThat(saved.getLastSeenAt()).isNotNull();

        // Raw listing is saved
        verify(rawListingRepository).save(any(RawListing.class));

        // Photos are saved for each URL
        ArgumentCaptor<ListingPhoto> photoCaptor = ArgumentCaptor.forClass(ListingPhoto.class);
        verify(listingPhotoRepository, times(2)).save(photoCaptor.capture());

        List<ListingPhoto> photos = photoCaptor.getAllValues();
        assertThat(photos).hasSize(2);
        assertThat(photos)
                .extracting(ListingPhoto::getPhotoUrl)
                .containsExactly("https://img1", "https://img2");

        // Mark-missing is called once
        verify(listingRepository).markMissingListingsInactive(eq(source), eq(active), eq(removed), any(OffsetDateTime.class));
    }

    @Test
    void ingest_usesFallbackJsonWhenSerializationFails() throws Exception {
        ListingSource source = new ListingSource();
        ListingStatus active = new ListingStatus();
        ListingStatus removed = new ListingStatus();

        ListingDto dto = mock(ListingDto.class);
        when(dto.externalId()).thenReturn("ext-2");
        when(dto.canonicalUrl()).thenReturn("https://pararius.nl/listing/2");
        when(dto.photoUrls()).thenReturn(List.of()); // use image/photosCount branch

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.of(source));
        when(listingStatusRepository.findByCode("ACTIVE"))
                .thenReturn(Optional.of(active));
        when(listingStatusRepository.findByCode("REMOVED"))
                .thenReturn(Optional.of(removed));

        RawListing raw = new RawListing();
        when(rawListingRepository.findBySourceAndExternalId(source, "ext-2"))
                .thenReturn(Optional.of(raw));

        when(listingRepository.findBySourceCodeAndExternalId("PARARIUS", "ext-2"))
                .thenReturn(Optional.empty());
        when(listingRepository.save(any(Listing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ObjectMapper fails → toJson() falls back to "{}"
        when(objectMapper.writeValueAsString(dto))
                .thenThrow(new JsonProcessingException("boom") {});

        when(listingRepository.markMissingListingsInactive(eq(source), eq(active), eq(removed), any(OffsetDateTime.class)))
                .thenReturn(0);

        service.ingest(List.of(dto));

        // Raw listing got fallback payload
        assertThat(raw.getPayloadJson()).isEqualTo("{}");
        // Content hash may be null if computeContentHash hit runtime error, but
        // at least the call should not blow up.
        verify(rawListingRepository).save(raw);
    }

    @Test
    void ingest_keepsPetsAllowedTrueForExistingListing() throws Exception {
        ListingSource source = new ListingSource();
        ListingStatus active = new ListingStatus();
        ListingStatus removed = new ListingStatus();
        Agency agency = new Agency();

        ListingDto dto = mock(ListingDto.class);
        when(dto.externalId()).thenReturn("ext-3");
        when(dto.canonicalUrl()).thenReturn("https://pararius.nl/listing/3");
        when(dto.photoUrls()).thenReturn(List.of("https://img1"));

        when(objectMapper.writeValueAsString(dto))
                .thenReturn("{\"externalId\":\"ext-3\"}");

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.of(source));
        when(listingStatusRepository.findByCode("ACTIVE"))
                .thenReturn(Optional.of(active));
        when(listingStatusRepository.findByCode("REMOVED"))
                .thenReturn(Optional.of(removed));
        when(agencyRepository.findByExternalIdAndSource("PARARIUS_AGENCY", source))
                .thenReturn(Optional.of(agency));

        when(rawListingRepository.findBySourceAndExternalId(source, "ext-3"))
                .thenReturn(Optional.empty());

        Listing existing = new Listing();
        existing.setId(123L);
        existing.setPetsAllowed(true); // important!
        when(listingRepository.findBySourceCodeAndExternalId("PARARIUS", "ext-3"))
                .thenReturn(Optional.of(existing));

        when(listingRepository.save(any(Listing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(listingRepository.markMissingListingsInactive(eq(source), eq(active), eq(removed), any(OffsetDateTime.class)))
                .thenReturn(0);

        service.ingest(List.of(dto));

        // Existing listing id != null → isNew = false → petsAllowed should not be forced to false
        assertThat(existing.isPetsAllowed()).isTrue();
    }

    @Test
    void ingest_logsAndContinuesWhenUpsertFailsForOneDto() throws Exception {
        ListingSource source = new ListingSource();
        ListingStatus active = new ListingStatus();
        ListingStatus removed = new ListingStatus();

        ListingDto dto1 = mock(ListingDto.class);
        ListingDto dto2 = mock(ListingDto.class);

        when(dto1.externalId()).thenReturn("ext-1");
        when(dto1.canonicalUrl()).thenReturn("https://pararius.nl/listing/1");
        when(dto2.externalId()).thenReturn("ext-2");
        when(dto2.canonicalUrl()).thenReturn("https://pararius.nl/listing/2");

        when(dto1.photoUrls()).thenReturn(List.of("https://img1"));
        when(dto2.photoUrls()).thenReturn(List.of("https://img2"));

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.of(source));
        when(listingStatusRepository.findByCode("ACTIVE"))
                .thenReturn(Optional.of(active));
        when(listingStatusRepository.findByCode("REMOVED"))
                .thenReturn(Optional.of(removed));

        when(rawListingRepository.findBySourceAndExternalId(eq(source), anyString()))
                .thenReturn(Optional.empty());

        when(listingRepository.findBySourceCodeAndExternalId(eq("PARARIUS"), anyString()))
                .thenReturn(Optional.empty());

        when(objectMapper.writeValueAsString(any(ListingDto.class)))
                .thenReturn("{\"ok\":true}");

        // First save of listing throws, second succeeds
        when(listingRepository.save(any(Listing.class)))
                .thenThrow(new RuntimeException("boom on first"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(listingRepository.markMissingListingsInactive(eq(source), eq(active), eq(removed), any(OffsetDateTime.class)))
                .thenReturn(0);

        service.ingest(List.of(dto1, dto2));

        // One failure, one success → listingRepository.save called twice
        verify(listingRepository, times(2)).save(any(Listing.class));
        // But photos should be saved only for the successful listing (2nd)
        verify(listingPhotoRepository, atLeastOnce()).save(any(ListingPhoto.class));
    }

    @Test
    void ingest_createsPhotosFromImageWhenPhotoUrlsNullOrEmpty() throws Exception {
        ListingSource source = new ListingSource();
        ListingStatus active = new ListingStatus();
        ListingStatus removed = new ListingStatus();

        ListingDto dto = mock(ListingDto.class);
        when(dto.externalId()).thenReturn("ext-4");
        when(dto.canonicalUrl()).thenReturn("https://pararius.nl/listing/4");
        when(dto.photoUrls()).thenReturn(null); // use fallback branch
        when(dto.image()).thenReturn("https://fallback-img-4");
        // photosCount() only used when photoUrls is null/empty
        when(dto.photosCount()).thenReturn(5);

        when(objectMapper.writeValueAsString(dto))
                .thenReturn("{\"externalId\":\"ext-4\"}");

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.of(source));
        when(listingStatusRepository.findByCode("ACTIVE"))
                .thenReturn(Optional.of(active));
        when(listingStatusRepository.findByCode("REMOVED"))
                .thenReturn(Optional.of(removed));

        when(rawListingRepository.findBySourceAndExternalId(source, "ext-4"))
                .thenReturn(Optional.empty());

        when(listingRepository.findBySourceCodeAndExternalId("PARARIUS", "ext-4"))
                .thenReturn(Optional.empty());

        when(listingRepository.save(any(Listing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(listingRepository.markMissingListingsInactive(eq(source), eq(active), eq(removed), any(OffsetDateTime.class)))
                .thenReturn(0);

        service.ingest(List.of(dto));

        ArgumentCaptor<Listing> captor = ArgumentCaptor.forClass(Listing.class);
        verify(listingRepository, atLeastOnce()).save(captor.capture());

        Listing saved = captor.getValue();
        assertThat(saved.getPrimaryPhotoUrl()).isEqualTo("https://fallback-img-4");
        assertThat(saved.getPhotosCount()).isEqualTo(5);
    }

    @Test
    void ingest_logsAndSkipsPhotoWhenPhotoSaveFails() throws Exception {
        ListingSource source = new ListingSource();
        ListingStatus active = new ListingStatus();
        ListingStatus removed = new ListingStatus();

        ListingDto dto = mock(ListingDto.class);
        when(dto.externalId()).thenReturn("ext-5");
        when(dto.canonicalUrl()).thenReturn("https://pararius.nl/listing/5");
        when(dto.photoUrls()).thenReturn(List.of("https://img-ok", "https://img-fail"));

        when(objectMapper.writeValueAsString(dto))
                .thenReturn("{\"externalId\":\"ext-5\"}");

        when(listingSourceRepository.findByCode("PARARIUS"))
                .thenReturn(Optional.of(source));
        when(listingStatusRepository.findByCode("ACTIVE"))
                .thenReturn(Optional.of(active));
        when(listingStatusRepository.findByCode("REMOVED"))
                .thenReturn(Optional.of(removed));

        when(rawListingRepository.findBySourceAndExternalId(source, "ext-5"))
                .thenReturn(Optional.empty());

        when(listingRepository.findBySourceCodeAndExternalId("PARARIUS", "ext-5"))
                .thenReturn(Optional.empty());

        when(listingRepository.save(any(Listing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // First photo save OK, second throws DataAccessException
        when(listingPhotoRepository.save(any(ListingPhoto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0))
                .thenThrow(new DataAccessException("photo fail") {});


        when(listingRepository.markMissingListingsInactive(eq(source), eq(active), eq(removed), any(OffsetDateTime.class)))
                .thenReturn(0);

        service.ingest(List.of(dto));

        // Even though one photo save failed, the ingest should complete
        verify(listingPhotoRepository, times(2)).save(any(ListingPhoto.class));
    }
}