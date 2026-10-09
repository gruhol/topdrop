package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.UpdateInventoryProductsStockAndPriceResponse;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.UpdateInventoryProductsStockRequest;
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
public class UpdateInventoryProductsStockBaselinkerService
        extends BaselinkerWebClientService
        implements BaselinkerSendable<UpdateInventoryProductsStockAndPriceResponse, UpdateInventoryProductsStockRequest> {

    private final ProductRepository productRepository;

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "updateInventoryProductsStock";
    }

    @Override
    public UpdateInventoryProductsStockAndPriceResponse sendRequest(UpdateInventoryProductsStockRequest request) {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(request.getRequest());
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        UpdateInventoryProductsStockAndPriceResponse updateInventoryStock = mapToResponse(res, UpdateInventoryProductsStockAndPriceResponse.class);
                        if ("SUCCESS".equals(updateInventoryStock.getStatus())) {
                            updateStatuses(request.getProducts(), updateInventoryStock.getWarnings());
                        }
                        return updateInventoryStock;
                    })
                    .orElseThrow(() -> new RuntimeException("Error baselinker api"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
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
        products.forEach(prod -> {
            String baselinkerId = String.valueOf(prod.getExportLog().getBaselinkerId());
            if (rejectedIds.contains(baselinkerId)) {
                log.warn("Baselinker rejected stock update for product id={}, ean={}, baselinkerId={}: {}",
                        prod.getId(), prod.getEan(), baselinkerId, warnings.get(baselinkerId));
                prod.setSyncStatus(SyncStatus.ERROR);
            } else {
                prod.setSyncStatus(SyncStatus.SYNCED);
            }
        });
        productRepository.saveAll(products);
    }
}
