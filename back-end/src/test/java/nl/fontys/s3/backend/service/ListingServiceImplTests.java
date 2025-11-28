package nl.fontys.s3.backend.service;

import nl.fontys.s3.backend.dto.FilterCriteria;
import nl.fontys.s3.backend.dto.FilterOption;
import nl.fontys.s3.backend.dto.ListingDto;
import nl.fontys.s3.backend.entity.Listing;
import nl.fontys.s3.backend.repository.interfaces.ListingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceImplTests {

    @Mock
    private ListingRepository listingRepository;

    @InjectMocks
    private ListingServiceImpl listingService;


    @Test
    void getFeatured_usesLimitAndSortsByLastSeenAtDesc() {
        Page<Listing> page = new PageImpl<>(List.of());
        when(listingRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<ListingDto> result = listingService.getFeatured(10);

        assertThat(result).isEmpty();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository).findAll(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "lastSeenAt"));
    }

    @Test
    void getFeatured_clampsLimitBetween1And100() {
        when(listingRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        listingService.getFeatured(0);
        listingService.getFeatured(500);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository, times(2)).findAll(pageableCaptor.capture());

        List<Pageable> captured = pageableCaptor.getAllValues();
        assertThat(captured.get(0).getPageSize()).isEqualTo(1);
        assertThat(captured.get(1).getPageSize()).isEqualTo(100);
    }


    @Test
    void search_callsRepositoryWithSpecAndPageable() {
        when(listingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        List<ListingDto> result = listingService.search("eindhoven", 20);

        assertThat(result).isEmpty();
        verify(listingRepository).findAll(any(Specification.class), any(Pageable.class));
    }


    @Test
    void list_usesFallbackSortWhenPageableIsNull() {
        FilterCriteria criteria = new FilterCriteria(
                null, null, null,
                null, null,
                null, null,
                null, null,
                null, null,
                null
        );

        when(listingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<ListingDto> result = listingService.list(criteria, null);

        assertThat(result.getContent()).isEmpty();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(20); // default
        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "lastSeenAt"));
    }

    @Test
    void list_keepsProvidedPageableButAddsFallbackSortIfUnsorted() {
        FilterCriteria criteria = new FilterCriteria(
                null, null, null,
                null, null,
                null, null,
                null, null,
                null, null,
                null
        );

        Pageable requestPageable = PageRequest.of(2, 5); // unsorted

        when(listingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        listingService.list(criteria, requestPageable);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(listingRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable effective = pageableCaptor.getValue();
        assertThat(effective.getPageNumber()).isEqualTo(2);
        assertThat(effective.getPageSize()).isEqualTo(5);
        assertThat(effective.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "lastSeenAt"));
    }

    @Test
    void list_swapsMinAndMaxWhenReversed() {
        FilterCriteria criteria = new FilterCriteria(
                null,
                null,
                null,
                2000, 1000, // reversed price
                null,
                null,
                null,
                null,
                80, 40,     // reversed area
                null
        );

        when(listingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        listingService.list(criteria, PageRequest.of(0, 10));


        verify(listingRepository).findAll(any(Specification.class), any(Pageable.class));
    }


    @Test
    void getById_throwsNotFoundWhenMissing() {
        when(listingRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.getById(42L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Listing not found");
    }


    @Test
    void getCityOptions_sortsByValueAndTitleCasesLabels() {
        when(listingRepository.findAllDistinctCitiesUsed())
                .thenReturn(List.of("eindhoven", "AMSTERDAM", "rotterdam"));

        List<FilterOption> options = listingService.getCityOptions();

        // underlying "value" should be sorted lexicographically
        assertThat(options).extracting(FilterOption::value)
                .containsExactly("AMSTERDAM", "eindhoven", "rotterdam");

        // labels should be nicely title-cased
        assertThat(options).extracting(FilterOption::label)
                .containsExactly("Amsterdam", "Eindhoven", "Rotterdam");
    }
}
