package nl.fontys.s3.backend.scraper.pararius;

public class ListingItemParseException extends RuntimeException {

    private final int page;
    private final String citySlug;
    private final String searchPageUrl;

    public ListingItemParseException(String message,
                                     int page,
                                     String citySlug,
                                     String searchPageUrl,
                                     Throwable cause) {
        super(message, cause);
        this.page = page;
        this.citySlug = citySlug;
        this.searchPageUrl = searchPageUrl;
    }

    public int getPage() {
        return page;
    }

    public String getCitySlug() {
        return citySlug;
    }

    public String getSearchPageUrl() {
        return searchPageUrl;
    }
}