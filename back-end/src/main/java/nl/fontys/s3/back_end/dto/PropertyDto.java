package nl.fontys.s3.back_end.dto;

public record PropertyDto(
        String id,
        String title,
        String image,
        String price,
        String location
) {
}
