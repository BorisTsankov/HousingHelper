package nl.fontys.s3.back_end.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "listing_photo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"listing_id","photo_url"}))
public class ListingPhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(name = "photo_url", nullable = false, columnDefinition = "TEXT")
    private String photoUrl;

    @Column(length = 64)
    private String checksum;

    private Short position;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    public Short getPosition() { return position; }
    public void setPosition(Short position) { this.position = position; }

}