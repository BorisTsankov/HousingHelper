package nl.fontys.s3.backend.scraper.pararius;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class ParariusScheduler {

    private final ParariusScraperUsingListingDto scraper;
    private final ParariusIngestFromDtoService ingestService;
    private static final Logger log = LoggerFactory.getLogger(ParariusScheduler.class);

    public ParariusScheduler(ParariusScraperUsingListingDto scraper,
                             ParariusIngestFromDtoService ingestService) {
        this.scraper = scraper;
        this.ingestService = ingestService;
    }

    // Every 30 minutes
//    @Scheduled(cron = "0 */30 * * * *")
    @Scheduled(fixedDelay = 10000)

    public void scrapeEindhoven() {
        log.info("Starting scheduled Pararius scrape for Eindhoven");

        var dtos = scraper.scrapeCity("eindhoven", 3);
        ingestService.ingest(dtos);

        log.info("Finished scheduled Pararius scrape for Eindhoven. Listings scraped: {}", dtos.size());
    }

}
