package nl.fontys.s3.back_end.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "listing",
        uniqueConstraints = @UniqueConstraint(columnNames = {"source_id", "external_id"})
)
public class Listing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "source_id")
    private ListingSource source;

    @Column(name = "external_id", nullable = false, length = 128)
    private String externalId;

    @Column(name = "canonical_url", columnDefinition = "TEXT")
    private String canonicalUrl;

    @Column(name = "first_seen_at", nullable = false)
    private OffsetDateTime firstSeenAt = OffsetDateTime.now();

    @Column(name = "last_seen_at", nullable = false)
    private OffsetDateTime lastSeenAt = OffsetDateTime.now();

    @ManyToOne(optional = false) @JoinColumn(name = "status_id")
    private ListingStatus status;

    @Column(length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne @JoinColumn(name = "property_type_id")
    private PropertyType propertyType;

    @ManyToOne @JoinColumn(name = "furnishing_type_id")
    private FurnishingType furnishingType;

    @Column(name = "energy_label", length = 8)
    private String energyLabel;

    @Column(name = "rent_amount", precision = 12, scale = 2)
    private BigDecimal rentAmount;

    @ManyToOne @JoinColumn(name = "rent_period_id")
    private RentPeriod rentPeriod;

    @Column(precision = 12, scale = 2)
    private BigDecimal deposit;

    @Column(name = "area_m2", precision = 10, scale = 2)
    private BigDecimal areaM2;

    @Column(precision = 5, scale = 2)
    private BigDecimal rooms;

    @Column(precision = 5, scale = 2)
    private BigDecimal bedrooms;

    @Column(precision = 5, scale = 2)
    private BigDecimal bathrooms;

    private LocalDate availableFrom;
    private LocalDate availableUntil;

    @Column(name = "minimum_lease_months")
    private Integer minimumLeaseMonths;

    @Column(length = 2)
    private String country;

    @Column(length = 120)
    private String city;

    @Column(length = 16)
    private String postalCode;

    @Column(length = 160)
    private String street;

    @Column(length = 32)
    private String houseNumber;

    @Column(length = 32)
    private String unit;

    private Double lat;
    private Double lon;

    @Column(name = "primary_photo_url", columnDefinition = "TEXT")
    private String primaryPhotoUrl;

    @Column(name = "photos_count")
    private Integer photosCount;

    @Column(name = "landlord_type", length = 16)
    private String landlordType; // "AGENCY" or "PRIVATE"

    @Column(name = "contact_email_hash", length = 64)
    private String contactEmailHash;

    @Column(name = "contact_phone_hash", length = 64)
    private String contactPhoneHash;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "ingest_job_id")
    private Long ingestJobId;

    // getters/setters


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ListingSource getSource() { return source; }
    public void setSource(ListingSource source) { this.source = source; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }

    public OffsetDateTime getFirstSeenAt() { return firstSeenAt; }
    public void setFirstSeenAt(OffsetDateTime firstSeenAt) { this.firstSeenAt = firstSeenAt; }

    public OffsetDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(OffsetDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }

    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus status) { this.status = status; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public PropertyType getPropertyType() { return propertyType; }
    public void setPropertyType(PropertyType propertyType) { this.propertyType = propertyType; }

    public FurnishingType getFurnishingType() { return furnishingType; }
    public void setFurnishingType(FurnishingType furnishingType) { this.furnishingType = furnishingType; }

    public String getEnergyLabel() { return energyLabel; }
    public void setEnergyLabel(String energyLabel) { this.energyLabel = energyLabel; }

    public BigDecimal getRentAmount() { return rentAmount; }
    public void setRentAmount(BigDecimal rentAmount) { this.rentAmount = rentAmount; }

    public RentPeriod getRentPeriod() { return rentPeriod; }
    public void setRentPeriod(RentPeriod rentPeriod) { this.rentPeriod = rentPeriod; }

    public BigDecimal getDeposit() { return deposit; }
    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }

    public BigDecimal getAreaM2() { return areaM2; }
    public void setAreaM2(BigDecimal areaM2) { this.areaM2 = areaM2; }

    public BigDecimal getRooms() { return rooms; }
    public void setRooms(BigDecimal rooms) { this.rooms = rooms; }

    public BigDecimal getBedrooms() { return bedrooms; }
    public void setBedrooms(BigDecimal bedrooms) { this.bedrooms = bedrooms; }

    public BigDecimal getBathrooms() { return bathrooms; }
    public void setBathrooms(BigDecimal bathrooms) { this.bathrooms = bathrooms; }

    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }

    public LocalDate getAvailableUntil() { return availableUntil; }
    public void setAvailableUntil(LocalDate availableUntil) { this.availableUntil = availableUntil; }

    public Integer getMinimumLeaseMonths() { return minimumLeaseMonths; }
    public void setMinimumLeaseMonths(Integer minimumLeaseMonths) { this.minimumLeaseMonths = minimumLeaseMonths; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public String getPrimaryPhotoUrl() { return primaryPhotoUrl; }
    public void setPrimaryPhotoUrl(String primaryPhotoUrl) { this.primaryPhotoUrl = primaryPhotoUrl; }

    public Integer getPhotosCount() { return photosCount; }
    public void setPhotosCount(Integer photosCount) { this.photosCount = photosCount; }

    public String getLandlordType() { return landlordType; }
    public void setLandlordType(String landlordType) { this.landlordType = landlordType; }

    public String getContactEmailHash() { return contactEmailHash; }
    public void setContactEmailHash(String contactEmailHash) { this.contactEmailHash = contactEmailHash; }

    public String getContactPhoneHash() { return contactPhoneHash; }
    public void setContactPhoneHash(String contactPhoneHash) { this.contactPhoneHash = contactPhoneHash; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public Long getIngestJobId() { return ingestJobId; }
    public void setIngestJobId(Long ingestJobId) { this.ingestJobId = ingestJobId; }

}