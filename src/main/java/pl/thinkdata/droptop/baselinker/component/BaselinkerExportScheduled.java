package pl.thinkdata.droptop.baselinker.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import pl.thinkdata.droptop.api.controller.UpdateProduct;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.service.AddInventoryProductBaselinkerService;
import pl.thinkdata.droptop.config.service.SystemSettingService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class BaselinkerExportScheduled {

    private final SystemSettingService systemSettingService;
    private final AddInventoryProductBaselinkerService addInventoryProductService;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void baselinkerAutoExportProducts() {
        boolean enabled = systemSettingService.getValue("baselinker_auto_export", Boolean.class);
        if (enabled) {
            try {
                sendProductsToBaseLinker();
            } catch (WebClientRequestException e) {
                log.info("Błąd połączenia z BaseLinker: {}", e.getMessage());
            } catch (Exception e) {
                log.info("Nieoczekiwany błąd w zadaniu Baselinker export: {}", e.getMessage());
            }

        } else {
            String now = Instant.ofEpochMilli(System.currentTimeMillis())
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("Eksport is stop: {}", now);
        }
    }

    @Scheduled(cron = "0 0 */2 * * *", zone = "Europe/Warsaw")
    public void platonAutoImportProducts() {

    }

    @Scheduled(cron = "0 0 * * * *", zone = "Europe/Warsaw")
    public void platonAutoImportStock() {
    }

    public void sendProductsToBaseLinker() {

        AddProductResponse result = addInventoryProductService.sendProducts();
        if (result.getStatus().equals("SUCCESS")) {
            System.out.println("Utworzono o id: " + result.getProductId());
        } else {
            System.out.println("Błąd.");
        }
    }
}
