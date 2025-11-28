package nl.fontys.s3.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "listing_status")
public class ListingStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(nullable = false, unique = true, length = 24)
    private String code;

    @Column(nullable = false, length = 80)
    private String label;

    @Column(nullable = false)
    private boolean isActive = true;

    public Short getId() { return id; }
    public void setId(Short id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

}