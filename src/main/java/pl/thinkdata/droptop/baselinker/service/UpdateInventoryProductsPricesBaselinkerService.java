package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.UpdateInventoryProductsPriceRequest;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.UpdateInventoryProductsStockAndPriceResponse;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.product.Product;
import pl.thinkdata.droptop.database.model.product.SyncStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateInventoryProductsPricesBaselinkerService
        extends BaselinkerWebClientService
        implements BaselinkerSendable<UpdateInventoryProductsStockAndPriceResponse, UpdateInventoryProductsPriceRequest> {

    private final ProductRepository productRepository;

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "updateInventoryProductsPrices";
    }

    @Override
    public UpdateInventoryProductsStockAndPriceResponse sendRequest(UpdateInventoryProductsPriceRequest request) {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(request.getRequest());
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        UpdateInventoryProductsStockAndPriceResponse updateInventoryPrice = mapToResponse(res, UpdateInventoryProductsStockAndPriceResponse.class);
                        if ("SUCCESS".equals(updateInventoryPrice.getStatus())) {
                            updateStatuses(request.getProducts(), updateInventoryPrice.getWarnings());
                        }
                        return updateInventoryPrice;
                    })
                    .orElseThrow(() -> new RuntimeException("Error baselinker api"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }

    private SyncStatus getSyncStatus(Product product) {
        if (product.getSyncStatus().equals(SyncStatus.PRICE_STOCK_UPDATE)) {
            return SyncStatus.STOCK_UPDATE;
        }
        return SyncStatus.SYNCED;
    }

    // produkty odrzucone przez Baselinker (warnings) dostają ERROR, reszta partii jest oznaczana jako wysłana,
    // żeby jeden błędny produkt nie blokował całej partii w kolejce
    private void updateStatuses(List<String> eans, Map<String, Object> warnings) {
        Set<String> rejectedIds = Optional.ofNullable(warnings)
                .map(Map::keySet)
                .orElse(Set.of())
                .stream()
                .map(key -> key.split(":")[0])
                .collect(Collectors.toSet());
        if (!rejectedIds.isEmpty()) {
            log.warn("Baselinker warnings: {}", warnings);
        }

        List<Product> products = productRepository.findByEanIn(eans);
        Set<String> sentIds = products.stream()
                .map(prod -> String.valueOf(prod.getExportLog().getBaselinkerId()))
                .collect(Collectors.toSet());
        if (!sentIds.containsAll(rejectedIds)) {
            // ostrzeżenia dotyczą ID, których nie wysłaliśmy – nie wiadomo, które produkty przeszły, więc statusy zostają bez zmian
            log.error("Baselinker warnings do not match sent product ids, statuses not changed");
            return;
        }
        products.forEach(prod -> {
            String baselinkerId = String.valueOf(prod.getExportLog().getBaselinkerId());
            if (rejectedIds.contains(baselinkerId)) {
                log.warn("Baselinker rejected price update for product id={}, ean={}, baselinkerId={}: {}",
                        prod.getId(), prod.getEan(), baselinkerId, warnings.get(baselinkerId));
                prod.setSyncStatus(SyncStatus.ERROR);
            } else {
                prod.setSyncStatus(getSyncStatus(prod));
            }
        });
        productRepository.saveAll(products);
    }
}
