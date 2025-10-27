package nl.fontys.s3.back_end.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "agency",
        uniqueConstraints = @UniqueConstraint(columnNames = {"source_id", "external_id"})
)
public class Agency {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "source_id")
    private ListingSource source;

    @Column(name = "external_id", nullable = false, length = 128)
    private String externalId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "website_url", length = 512)
    private String websiteUrl;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(length = 2)   private String country;
    @Column(length = 120) private String city;
    @Column(length = 16)  private String postalCode;
    @Column(length = 160) private String street;
    @Column(length = 32)  private String houseNumber;
    @Column(length = 32)  private String unit;

    @Column(name = "contact_email_hash", length = 64)
    private String contactEmailHash;

    @Column(name = "contact_phone_hash", length = 64)
    private String contactPhoneHash;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ListingSource getSource() { return source; }
    public void setSource(ListingSource source) { this.source = source; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

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

    public String getContactEmailHash() { return contactEmailHash; }
    public void setContactEmailHash(String contactEmailHash) { this.contactEmailHash = contactEmailHash; }

    public String getContactPhoneHash() { return contactPhoneHash; }
    public void setContactPhoneHash(String contactPhoneHash) { this.contactPhoneHash = contactPhoneHash; }
}