package nl.fontys.s3.backend.dto;

public record PriceBucket(String label, Integer min, Integer max) {}