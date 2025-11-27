package nl.fontys.s3.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "listing_price_history")
public class ListingPriceHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(name = "observed_at", nullable = false)
    private OffsetDateTime observedAt = OffsetDateTime.now();

    @Column(name = "rent_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal rentAmount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }

    public OffsetDateTime getObservedAt() { return observedAt; }
    public void setObservedAt(OffsetDateTime observedAt) { this.observedAt = observedAt; }

    public BigDecimal getRentAmount() { return rentAmount; }
    public void setRentAmount(BigDecimal rentAmount) { this.rentAmount = rentAmount; }

}