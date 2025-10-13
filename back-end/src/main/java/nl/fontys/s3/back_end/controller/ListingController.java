package nl.fontys.s3.back_end.controller;
import nl.fontys.s3.back_end.dto.PropertyDto;
import nl.fontys.s3.back_end.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")

public class ListingController {

    private final ListingService listingService;
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/featured")
    public ResponseEntity<List<PropertyDto>> getFeatured(
            @RequestParam(defaultValue = "12") int limit
    ) {
        return ResponseEntity.ok(listingService.getFeatured(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PropertyDto>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "24") int limit
    ){
        return ResponseEntity.ok(listingService.search(q, limit));
    }

    @GetMapping("/ping")
    public String ping() { return "pong"; }
}


