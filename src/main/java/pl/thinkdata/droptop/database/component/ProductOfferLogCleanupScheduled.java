package pl.thinkdata.droptop.database.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductOfferLogCleanupScheduled {

    private static final int RETENTION_DAYS = 90;
    private static final int BATCH_SIZE = 10000;

    private final JdbcTemplate jdbcTemplate;

    // Usuwanie partiami, żeby nie trzymać długich blokad na tabeli.
    // Data odcięcia liczona w Javie - działa zarówno na MySQL (prod), jak i H2 (dev).
    @Scheduled(cron = "0 0 4 * * *", zone = "Europe/Warsaw")
    public void cleanupOfferLog() {
        Timestamp cutoff = Timestamp.valueOf(LocalDateTime.now().minusDays(RETENTION_DAYS));
        int total = 0;
        int deleted;
        try {
            do {
                deleted = jdbcTemplate.update(
                        "DELETE FROM product_offer_log WHERE fetched_at < ? LIMIT " + BATCH_SIZE, cutoff);
                total += deleted;
            } while (deleted > 0);
            log.info("Czyszczenie product_offer_log: usunięto {} rekordów starszych niż {}", total, cutoff);
        } catch (Exception e) {
            log.error("Błąd czyszczenia product_offer_log po usunięciu {} rekordów", total, e);
        }
    }
}
