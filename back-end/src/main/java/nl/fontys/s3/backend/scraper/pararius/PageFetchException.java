package nl.fontys.s3.backend.scraper.pararius;

public class PageFetchException extends RuntimeException {
    public PageFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}