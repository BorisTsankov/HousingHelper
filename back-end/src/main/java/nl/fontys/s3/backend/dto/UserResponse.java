package nl.fontys.s3.backend.dto;

import java.time.OffsetDateTime;

public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private OffsetDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, String name, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}