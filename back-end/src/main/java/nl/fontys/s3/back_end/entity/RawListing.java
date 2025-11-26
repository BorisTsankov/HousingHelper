package nl.fontys.s3.back_end.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "raw_listing",
        uniqueConstraints = @UniqueConstraint(columnNames = {"source_id", "external_id"})
)
public class RawListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_id")
    private ListingSource source;

    @Column(name = "external_id", nullable = false, length = 128)
    private String externalId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "fetched_at", nullable = false)
    private OffsetDateTime fetchedAt = OffsetDateTime.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_json", nullable = false, columnDefinition = "jsonb")
    private String payloadJson;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ListingSource getSource() { return source; }
    public void setSource(ListingSource source) { this.source = source; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public OffsetDateTime getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(OffsetDateTime fetchedAt) { this.fetchedAt = fetchedAt; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
}
