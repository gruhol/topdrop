package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.UpdateInventoryProductsPriceRequest;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.UpdateInventoryProductsStockAndPriceResponse;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.model.SyncStatus;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateInventoryProductsPricesBaselinkerService
        extends BaselinkerService
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
                        if(updateInventoryPrice.getCounter() == request.getProducts().size()) {
                            List<Product> products = productRepository.findByEanIn(request.getProducts());
                            products.forEach(prod -> prod.setSyncStatus(SyncStatus.SYNCED));
                            productRepository.saveAll(products);
                        }
                        return updateInventoryPrice;
                    })
                    .orElseThrow(() -> new RuntimeException("Error baselinker api"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }
}
