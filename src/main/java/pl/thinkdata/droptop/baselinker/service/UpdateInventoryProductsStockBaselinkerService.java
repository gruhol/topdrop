package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.UpdateInventoryProductsStockAndPriceResponse;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.UpdateInventoryProductsStockRequest;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.model.SyncStatus;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateInventoryProductsStockBaselinkerService
        extends BaselinkerService
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
                        if(updateInventoryStock.getCounter() == request.getProducts().size()) {
                            List<Product> products = productRepository.findByEanIn(request.getProducts());
                            products.forEach(prod -> prod.setSyncStatus(SyncStatus.SYNCED));
                            productRepository.saveAll(products);
                        }
                        return updateInventoryStock;
                    })
                    .orElseThrow(() -> new RuntimeException("Error baselinker api"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }
}
