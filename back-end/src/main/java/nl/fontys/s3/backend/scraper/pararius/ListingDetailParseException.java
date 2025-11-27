package nl.fontys.s3.backend.scraper.pararius;

public class ListingDetailParseException extends RuntimeException {

    private final String detailUrl;

    public ListingDetailParseException(String message, String detailUrl, Throwable cause) {
        super(message, cause);
        this.detailUrl = detailUrl;
    }

    public String getDetailUrl() {
        return detailUrl;
    }
}
