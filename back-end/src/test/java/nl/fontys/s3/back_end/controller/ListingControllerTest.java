package nl.fontys.s3.back_end.controller;

import nl.fontys.s3.back_end.dto.*;
import nl.fontys.s3.back_end.service.serviceInterface.ListingService;
import nl.fontys.s3.back_end.security.JwtUtil;
import nl.fontys.s3.back_end.repository.repositoryInterface.UserRepository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
@AutoConfigureMockMvc(addFilters = false)
class ListingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ListingService listingService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;


    @Test
    void getOne_returnsListingDto() throws Exception {

        List<String> tags = List.of("tag1", "tag2", "tag3");
        ListingDto dto = new ListingDto(
                "10", "Test Listing", "img.jpg",
                "€1.000/mo", "Eindhoven",
                "€1.000/mo", "€2.000",
                BigDecimal.valueOf(1000), "monthly", BigDecimal.valueOf(2000),
                "Nice house", "active", "apartment", "furnished", "A",
                BigDecimal.valueOf(70), BigDecimal.valueOf(3),
                BigDecimal.valueOf(2), BigDecimal.valueOf(1),
                LocalDate.of(2025, 1, 1), null, 12,
                "NL", "Eindhoven", "5611AB",
                "Street", "10", "A",
                51.44, 5.47,
                3, "https://example.com",
                "EXT123", "Funda",
                tags
        );

        when(listingService.getById(10L)).thenReturn(dto);

        mvc.perform(get("/api/listings/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("10"))
                .andExpect(jsonPath("$.title").value("Test Listing"))
                .andExpect(jsonPath("$.location").value("Eindhoven"));
    }


    @Test
    void list_returnsPagedResults() throws Exception {

        List<String> tags = List.of("tag1", "tag2", "tag3");
        ListingDto dto = new ListingDto(
                "10",
                "Test Listing",
                "img.jpg",
                "€1.000/mo",
                "Eindhoven",

                "€1.000/mo",
                "€2.000",

                BigDecimal.valueOf(1000),
                "monthly",
                BigDecimal.valueOf(2000),

                "Nice house",
                "active",
                "apartment",
                "furnished",
                "A",

                BigDecimal.valueOf(70),
                BigDecimal.valueOf(3),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(1),

                LocalDate.of(2025, 1, 1),
                null,
                12,

                "NL",
                "Eindhoven",
                "5611AB",
                "Street",
                "10",
                "A",

                51.44,
                5.47,

                3,
                "https://example.com",
                "EXT123",
                "Funda",
                tags
        );

        PageImpl<ListingDto> page = new PageImpl<>(
                List.of(dto),
                PageRequest.of(0, 12),
                1
        );

        when(listingService.list(any(), any())).thenReturn(page);

        mvc.perform(get("/api/listings")
                        .param("q", "Tilburg")
                        .param("minPrice", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items[0].id").value("10"));
    }

    @Test
    void filters_returnsFilterGroup() throws Exception {

        when(listingService.getCityOptions()).thenReturn(
                List.of(
                        new FilterOption("Eindhoven", "eindhoven"),
                        new FilterOption("Tilburg", "tilburg")
                )
        );

        mvc.perform(get("/api/listings/filters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cities.length()").value(2))
                .andExpect(jsonPath("$.cities[0].value").value("eindhoven"));
    }


    @Test
    void filters_withListingsScope_addsBedroomBathroomFilters() throws Exception {

        when(listingService.getCityOptions()).thenReturn(
                List.of(new FilterOption("Eindhoven", "eindhoven"))
        );

        mvc.perform(get("/api/listings/filters?scope=listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bedrooms.length()").value(4))
                .andExpect(jsonPath("$.bathrooms.length()").value(2))
                .andExpect(jsonPath("$.cities[0].value").value("eindhoven"));
    }


    @Test
    void list_removesDisallowedSortFields() throws Exception {

        when(listingService.list(any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 12), 0));

        mvc.perform(get("/api/listings")
                        .param("sort", "id,asc")
                        .param("sort", "lastSeenAt,desc"))
                .andExpect(status().isOk());
    }


    @Test
    void list_usesProvidedPageable() throws Exception {

        when(listingService.list(any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(2, 12), 0));

        mvc.perform(get("/api/listings?page=2&size=12"))
                .andExpect(status().isOk());
    }
}
