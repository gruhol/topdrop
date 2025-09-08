package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.dto.Product;
import pl.thinkdata.droptop.baselinker.mapper.ProductMapper;
import pl.thinkdata.droptop.common.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddInventoryProductBaselinkerService extends BaselinkerService implements BaselinkerSendable<Product, pl.thinkdata.droptop.database.model.Product, AddProductResponse> {

    private final ProductRepository productRepository;
    private final BaselinkerLogService baselinkerLogService;

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "addInventoryProduct";
    }


    public AddProductResponse sendProduct(String ean) {
        pl.thinkdata.droptop.database.model.Product product = productRepository.findByEan(ean)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma takiego produktu"));

        return sendRequest(ProductMapper.map(product), product);
    }

    @Override
    public AddProductResponse sendRequest(Product productDto, pl.thinkdata.droptop.database.model.Product product) {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(productDto);
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        AddProductResponse addProductResponse = mapToAddProductResponse(res);
                        baselinkerLogService.sendSuccesExport(product, addProductResponse);
                        return addProductResponse;
                    })
                    .orElseThrow(() -> new RuntimeException("Empty response"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }

    private AddProductResponse mapToAddProductResponse(ResponseEntity<String> response) {
        try {
            return new ObjectMapper().readValue(response.getBody(), AddProductResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization Error", e);
        }
    }
}
