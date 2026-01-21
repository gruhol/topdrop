package pl.thinkdata.droptop.baselinker.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import pl.thinkdata.droptop.api.controller.PlatonApiController;
import pl.thinkdata.droptop.api.dto.UpdateProductInfo;
import pl.thinkdata.droptop.baselinker.dto.addCategory.AddCategoryResponse;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.service.AddCategoryProductBaselinkerService;
import pl.thinkdata.droptop.baselinker.service.AddInventoryProductBaselinkerService;
import pl.thinkdata.droptop.common.exception.NotFoundFileToExportException;
import pl.thinkdata.droptop.config.service.SystemSettingService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BaselinkerExportScheduled {

    private final SystemSettingService systemSettingService;
    private final AddInventoryProductBaselinkerService addInventoryProductService;
    private final AddCategoryProductBaselinkerService addCategoryProductService;
    private final PlatonApiController platonApiController;

    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void baselinkerAutoExportProducts() {
        boolean enabled = systemSettingService.getValue("baselinker_auto_export", Boolean.class);
        if (enabled) {
            try {
                sendProductsToBaseLinker();
            } catch (WebClientRequestException e) {
                log.info("Błąd połączenia z BaseLinker: {}", e.getMessage());
            } catch (NotFoundFileToExportException e) {
                log.info("Brak nowych produktów do exportu");
            } catch (Exception e) {
                log.info("Nieoczekiwany błąd w zadaniu Baselinker export: {}", e.getMessage());
            }

        } else {
            log.info("Eksport is stop: {}", getCorrentDate());
        }
    }

    @Scheduled(cron = "0 15 */3 * * *", zone = "Europe/Warsaw")
    public void baselinkerAutoExportCategory() {
        StringBuilder message = new StringBuilder();
        List<AddCategoryResponse> addCategoryResponse = addCategoryProductService.sendCategories();
        String now = Instant.ofEpochMilli(System.currentTimeMillis())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        boolean allSuccess = addCategoryResponse.stream()
                .map(AddCategoryResponse::getStatus)
                .allMatch("SUCCESS"::equals);
        if (allSuccess) {
            message.append("Exportowano nowe kategorie do baselinkera o: ").append(now);
        } else {
            message.append("Błąd: ");
            addCategoryResponse.stream()
                    .filter(status -> status.getStatus().equals("ERROR"))
                    .map(toString -> toString.getCategory_id() + " - Error: " + toString.getError_code() + ", Message: " + toString.getError_message())
                    .forEach(message::append);
        }
        log.info("Category export -> {} ", message);
    }

    @Scheduled(cron = "0 0 */3 * * *", zone = "Europe/Warsaw")
    public void platonAutoImportProducts() {
        boolean enabled = systemSettingService.getValue("baselinker_auto_export", Boolean.class);
        if (enabled) {
            UpdateProductInfo info = platonApiController.getProductsFromApi(10000);
            log.info("Inport produktów z Platon: nowe produty {}, zaaktualizowane: {}", info.getNewprod() ,info.getUpdate());
        } else {
            log.info("Import produktów wyłączony. Data: {}", getCorrentDate());
        }
    }

    // @Scheduled(cron = "0 0 * * * *", zone = "Europe/Warsaw")
    public void platonAutoImportStock() {
        boolean enabled = systemSettingService.getValue("baselinker_auto_export", Boolean.class);
        if (enabled) {
            int stockUpdateCount = platonApiController.getStockFromApi(10000);
            log.info("Inport stanów z Platon: {} nowych rekordów.", stockUpdateCount);
        } else {
            log.info("Import stanów wyłączony. Data: {}", getCorrentDate());
        }
    }

    public void sendProductsToBaseLinker() throws NotFoundFileToExportException {
        AddProductResponse result = addInventoryProductService.sendProducts();
        if (result.getStatus().equals("SUCCESS")) {
            log.info("Utworzono o id: {} Data: {}", result.getProductId(), getCorrentDate());
        } else {

            log.info("Bład wysłania produktu o id: {} Data: {}, Error: {}", result.getProductId(), getCorrentDate(), result.getError_message());
        }
    }

    private String getCorrentDate() {
        return Instant.ofEpochMilli(System.currentTimeMillis())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
