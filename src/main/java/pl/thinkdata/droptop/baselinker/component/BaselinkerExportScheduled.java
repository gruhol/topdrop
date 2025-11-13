package pl.thinkdata.droptop.baselinker.component;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.service.AddInventoryProductBaselinkerService;
import pl.thinkdata.droptop.config.service.SystemSettingService;

@Component
@RequiredArgsConstructor
public class BaselinkerExportScheduled {

    private final SystemSettingService systemSettingService;
    private final AddInventoryProductBaselinkerService addInventoryProductService;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void exportProducts() {
        boolean enabled = systemSettingService.getValue("baselinker_auto_export", Boolean.class);
        if (enabled) {
            try {
                sendProductsToBaseLinker();
            } catch (WebClientRequestException e) {
                System.out.println("Błąd połączenia z BaseLinker: {}" + e.getMessage());
            } catch (Exception e) {
                System.out.println("Nieoczekiwany błąd w zadaniu Baselinker export: " +  e);
            }

        } else {
            System.out.println("Eksport stopped: " + System.currentTimeMillis());
        }
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
