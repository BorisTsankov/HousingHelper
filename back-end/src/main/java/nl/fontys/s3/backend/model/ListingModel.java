package nl.fontys.s3.backend.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class ListingModel {

    private Long id;

    private String sourceName;
    private String externalId;
    private String canonicalUrl;

    private OffsetDateTime firstSeenAt;
    private OffsetDateTime lastSeenAt;

    private String status;

    private String title;
    private String description;

    private String propertyType;
    private String furnishingType;

    private String energyLabel;

    private BigDecimal rentAmount;
    private String rentPeriod;

    private BigDecimal deposit;
    private BigDecimal areaM2;

    private BigDecimal rooms;
    private BigDecimal bedrooms;
    private BigDecimal bathrooms;

    private LocalDate availableFrom;
    private LocalDate availableUntil;

    private Integer minimumLeaseMonths;

    private String country;
    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;
    private String unit;

    private Double lat;
    private Double lon;

    private String primaryPhotoUrl;
    private Integer photosCount;

    private String agencyName;

    private String contentHash;
    private Long ingestJobId;

    private Boolean petsAllowed;

    private List<String> photoUrls;


    // ----- GETTERS & SETTERS -----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }

    public OffsetDateTime getFirstSeenAt() { return firstSeenAt; }
    public void setFirstSeenAt(OffsetDateTime firstSeenAt) { this.firstSeenAt = firstSeenAt; }

    public OffsetDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(OffsetDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public String getFurnishingType() { return furnishingType; }
    public void setFurnishingType(String furnishingType) { this.furnishingType = furnishingType; }

    public String getEnergyLabel() { return energyLabel; }
    public void setEnergyLabel(String energyLabel) { this.energyLabel = energyLabel; }

    public BigDecimal getRentAmount() { return rentAmount; }
    public void setRentAmount(BigDecimal rentAmount) { this.rentAmount = rentAmount; }

    public String getRentPeriod() { return rentPeriod; }
    public void setRentPeriod(String rentPeriod) { this.rentPeriod = rentPeriod; }

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

    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public Long getIngestJobId() { return ingestJobId; }
    public void setIngestJobId(Long ingestJobId) { this.ingestJobId = ingestJobId; }

    public Boolean getPetsAllowed() { return petsAllowed; }
    public void setPetsAllowed(Boolean petsAllowed) { this.petsAllowed = petsAllowed; }

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
}
