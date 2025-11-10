package nl.fontys.s3.back_end.service;


import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.mapper.PropertyMapper;
import nl.fontys.s3.back_end.model.Listing;
import nl.fontys.s3.back_end.repository.ListingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

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


    @Test
    void getFeatured_returnsMappedDtos_andUsesDescSort() {
        int limit = 2;
        Listing l1 = mock(Listing.class);
        Listing l2 = mock(Listing.class);

        when(listingRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(l1, l2)));

        PropertyDto d1 = mock(PropertyDto.class);
        PropertyDto d2 = mock(PropertyDto.class);

        try (MockedStatic<PropertyMapper> mapper = Mockito.mockStatic(PropertyMapper.class)) {
            mapper.when(() -> PropertyMapper.toPropertyDto(l1)).thenReturn(d1);
            mapper.when(() -> PropertyMapper.toPropertyDto(l2)).thenReturn(d2);

            // IMPORTANT: call the SUT while the static mock is still open
            List<PropertyDto> list = listingService.getFeatured(limit);
            assertEquals(List.of(d1, d2), list);
        }

        verify(listingRepository).findAll(pageableCaptor.capture());
        Pageable used = pageableCaptor.getValue();
        assertEquals(0, used.getPageNumber());
        assertEquals(limit, used.getPageSize());
        assertTrue(used.getSort().getOrderFor("lastSeenAt").isDescending());
    }
}