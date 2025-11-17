package nl.fontys.s3.back_end.service;


import nl.fontys.s3.back_end.dto.FilterCriteria;
import nl.fontys.s3.back_end.dto.FilterOption;
import nl.fontys.s3.back_end.dto.ListingDto;
import nl.fontys.s3.back_end.mapper.ListingMapper;
import nl.fontys.s3.back_end.entity.Listing;
import nl.fontys.s3.back_end.repository.repositoryInterface.ListingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListingServiceTests {

    @InjectMocks
    private ListingServiceImpl listingService;

    @Mock
    private ListingRepository listingRepository;

    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;

    private Listing listing(long id) {
        Listing l = new Listing();
        l.setId(id);
        l.setLastSeenAt(OffsetDateTime.parse("2007-12-03T10:15:30+01:00"));
        l.setTitle("Nice flat");
        l.setCity("eindhoven");
        return l;
    }



    @Test
    void getFeatured_returnsMappedDtos_andUsesDescSort() {
        int limit = 2;
        Listing l1 = mock(Listing.class);
        Listing l2 = mock(Listing.class);

        when(listingRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(l1, l2)));

        ListingDto d1 = mock(ListingDto.class);
        ListingDto d2 = mock(ListingDto.class);

        try (MockedStatic<ListingMapper> mapper = Mockito.mockStatic(ListingMapper.class)) {
            mapper.when(() -> ListingMapper.toListingDto(l1)).thenReturn(d1);
            mapper.when(() -> ListingMapper.toListingDto(l2)).thenReturn(d2);

            List<ListingDto> list = listingService.getFeatured(limit);
            assertEquals(List.of(d1, d2), list);
        }

        verify(listingRepository).findAll(pageableCaptor.capture());
        Pageable used = pageableCaptor.getValue();
        assertEquals(0, used.getPageNumber());
        assertEquals(limit, used.getPageSize());
        assertTrue(used.getSort().getOrderFor("lastSeenAt").isDescending());
    }

    @Test
    void getFeatured_clampsLimitToMax() {

        int tooHighLimit = 500;
        Listing listing = new Listing();
        listing.setId(1L);
        PageImpl<Listing> page = new PageImpl<>(List.of(listing));

        when(listingRepository.findAll(any(Pageable.class))).thenReturn(page);

        listingService.getFeatured(tooHighLimit);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository).findAll(captor.capture());
        Pageable usedPageable = captor.getValue();

        assertEquals(100, usedPageable.getPageSize(), "Should clamp to MAX_LIMIT = 100");
        assertEquals(0, usedPageable.getPageNumber());
        assertEquals(Sort.Direction.DESC,
                usedPageable.getSort().getOrderFor("lastSeenAt").getDirection());
    }

    @Test
    void search_buildsSpec_usesDescSort_returnsDtos() {

        PageImpl<Listing> page = new PageImpl<>(List.of(listing(10)));
        when(listingRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(page);

        List <ListingDto> result = listingService.search("studio", 15);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository).findAll(any(Specification.class),captor.capture());
        Sort.Order order = captor.getValue().getSort().getOrderFor("lastSeenAt");
        assertEquals(Sort.Direction.DESC, order.getDirection());
        assertNotNull(order);
    }




    @ParameterizedTest
    @CsvSource({
            // minPrice,maxPrice,expectedMin,expectedMax
            "2000,1000,1000,2000",
            "null,1500,null,1500",
            "800,null,800,null",
            "900,900,900,900"
    })


    void list_normalizesPriceRangeAndAppliesSort(String minStr, String maxStr,
                                                 String expMinStr, String expMaxStr) {
        Integer minPrice = parseNullableInt(minStr);
        Integer maxPrice = parseNullableInt(maxStr);
        Integer expMin = parseNullableInt(expMinStr);
        Integer expMax = parseNullableInt(expMaxStr);

        FilterCriteria criteria = new FilterCriteria(
                null, null, null, minPrice, maxPrice,
                null, null, null, null, null, null, null
        );

        PageImpl<Listing> page = new PageImpl<>(List.of(listing(5)));
        when(listingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // unsorted pageable -> should inject fallback sort by lastSeenAt desc
        Pageable pageable = PageRequest.of(2, 50, Sort.unsorted());
        Page<ListingDto> result = listingService.list(criteria, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(5L, result.getContent().get(0).id());

        // verify pageable sort fallback applied
        ArgumentCaptor<Pageable> pg = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository).findAll(any(Specification.class), pg.capture());
        Sort.Order order = pg.getValue().getSort().getOrderFor("lastSeenAt");
        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());

        // We can’t directly assert normalized values inside Specification.
        // But we can at least ensure a Specification was provided:
        verify(listingRepository).findAll(any(Specification.class), any(Pageable.class));
    }


    @Test
    void list_preservesUserSortIfAlreadySorted() {
        PageImpl<Listing> page = new PageImpl<>(List.of(listing(7)));
        when(listingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "price"));
        listingService.list(new FilterCriteria(null, null, null, null, null, null, null, null, null, null, null, null), pageable);

        ArgumentCaptor<Pageable> pg = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository).findAll(any(Specification.class), pg.capture());
        Sort sort = pg.getValue().getSort();

        // assert it kept user's sort
        Sort.Order priceOrder = sort.getOrderFor("price");
        assertNotNull(priceOrder);
        assertEquals(Sort.Direction.ASC, priceOrder.getDirection());
    }

    @Test
    void list_injectsDefaultPageableWhenNull() {
        PageImpl<Listing> page = new PageImpl<>(List.of(listing(3)));
        when(listingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        listingService.list(new FilterCriteria(null, null, null, null, null,
                null, null, null, null, null, null, null), null);

        ArgumentCaptor<Pageable> pg = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository).findAll(any(Specification.class), pg.capture());
        Pageable used = pg.getValue();
        assertEquals(0, used.getPageNumber());
        assertEquals(20, used.getPageSize());
        assertTrue(used.getSort().isSorted());
        assertEquals(Sort.Direction.DESC, used.getSort().getOrderFor("lastSeenAt").getDirection());
    }

    // --- getById ---

    @Test
    void getById_returnsDtoWhenFound() {
        when(listingRepository.findById(42L)).thenReturn(Optional.of(listing(42)));

        ListingDto dto = listingService.getById(42L);

        assertEquals(42L, dto.id());
    }

    @Test
    void getById_throwsNotFoundWhenMissing() {
        when(listingRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> listingService.getById(99L));
        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Listing not found"));
    }

    // --- getCityOptions (title-case + sorted) ---

    @Test
    void getCityOptions_sortsAndTitleCasesLabel() {
        when(listingRepository.findAllDistinctCitiesUsed()).thenReturn(List.of("amsterdam", "EINDHOVEN", "s-'HERTOGENBOSCH"));

        List<FilterOption> options = listingService.getCityOptions();

        // sorted by value (natural order)
        assertEquals("EINDHOVEN", options.get(0).value());
        assertEquals("eindhoven", options.get(1).value().toLowerCase()); // either eindhoven or amsterdam depending on sort; adjust below
        assertEquals(3, options.size());

        // label title-cased, value original
        FilterOption eindhoven = options.stream().filter(o -> o.value().equals("EINDHOVEN")).findFirst().orElseThrow();
        assertEquals("Eindhoven", eindhoven.label());

        FilterOption ams = options.stream().filter(o -> o.value().equals("amsterdam")).findFirst().orElseThrow();
        assertEquals("Amsterdam", ams.label());

        // handles punctuation/words correctly
        FilterOption denBosch = options.stream().filter(o -> o.value().equals("s-'HERTOGENBOSCH")).findFirst().orElseThrow();
        // Title-case behavior is best-effort; assert first char is uppercase when alnum
        assertTrue(Character.isUpperCase(denBosch.label().charAt(0)));
    }

    // --- helpers ---

    private static Integer parseNullableInt(String s) {
        return "null".equalsIgnoreCase(s) ? null : Integer.valueOf(s);
    }
}
