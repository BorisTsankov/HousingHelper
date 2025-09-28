package nl.fontys.s3.back_end.dto;

public record UserRegistrationDto (
        String email,
        String name,
        String password
){
}
