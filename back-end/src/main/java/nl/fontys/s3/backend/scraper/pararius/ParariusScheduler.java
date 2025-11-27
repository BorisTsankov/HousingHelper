package nl.fontys.s3.backend.scraper.pararius;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ParariusScheduler {

    private final ParariusScraperUsingListingDto scraper;
    private final ParariusIngestFromDtoService ingestService;

    public ParariusScheduler(ParariusScraperUsingListingDto scraper,
                             ParariusIngestFromDtoService ingestService) {
        this.scraper = scraper;
        this.ingestService = ingestService;
    }

    // Every 30 minutes
//    @Scheduled(cron = "0 */30 * * * *")
    @Scheduled(fixedDelay = 10000)

    public void scrapeEindhoven() {
        try {
            var dtos = scraper.scrapeCity("eindhoven", 3);
            ingestService.ingest(dtos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
