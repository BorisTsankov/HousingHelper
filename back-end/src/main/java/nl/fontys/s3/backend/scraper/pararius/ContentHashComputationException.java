package nl.fontys.s3.backend.scraper.pararius;


public class ContentHashComputationException extends RuntimeException {

    private final String externalId;
    private final String canonicalUrl;

    public ContentHashComputationException(String message,
                                           String externalId,
                                           String canonicalUrl,
                                           Throwable cause) {
        super(message, cause);
        this.externalId = externalId;
        this.canonicalUrl = canonicalUrl;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }
}