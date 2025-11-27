package nl.fontys.s3.backend.scraper.pararius;

public class PageFetchException extends RuntimeException {

    private final String url;
    private final Integer page;
    private final String citySlug;

    // Simple constructor: URL only (e.g. detail pages)
    public PageFetchException(String message, String url, Throwable cause) {
        this(message, url, null, null, cause);
    }

    // Full constructor: URL + page + citySlug (e.g. search pages)
    public PageFetchException(String message,
                              String url,
                              Integer page,
                              String citySlug,
                              Throwable cause) {
        super(message, cause);
        this.url = url;
        this.page = page;
        this.citySlug = citySlug;
    }

    public String getUrl() {
        return url;
    }

    public Integer getPage() {
        return page;
    }

    public String getCitySlug() {
        return citySlug;
    }
}