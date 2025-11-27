package nl.fontys.s3.backend.dto;

public record UserRegistrationDto (
        String email,
        String name,
        String password
){
}
