package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.AddProductRequest;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.dto.Inventory;
import pl.thinkdata.droptop.baselinker.mapper.ProductMapper;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddInventoryProductBaselinkerService extends BaselinkerService implements BaselinkerSendable<AddProductResponse, AddProductRequest> {

    private final ProductRepository productRepository;
    private final BaselinkerLogService baselinkerLogService;
    private final GetPriceGroupsBaselinkerService getPriceGroupsService;
    private final GetInventoryBaselinkerService getInventoryService;

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "addInventoryProduct";
    }


    public AddProductResponse sendProduct(String ean) {
        pl.thinkdata.droptop.database.model.Product product = productRepository.findByEan(ean)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma takiego produktu"));
        Inventory inventory = getInventoryService.getDefaultInventory();

        AddProductRequest request = AddProductRequest.builder()
                .productDto(ProductMapper.map(product, inventory))
                .product(product)
                .build();
        return sendRequest(request);
    }

    public AddProductResponse sendProducts() {
        List<Product> productsToSend = productRepository.findTop100ByExportLogIsNull();
        Inventory inventory = getInventoryService.getDefaultInventory();
        if (productsToSend.isEmpty()) throw new IllegalArgumentException("Nie ma takich produktów");
        Set<AddProductResponse> productsSend = productsToSend.stream()
                .map(product -> AddProductRequest.builder()
                        .productDto(ProductMapper.map(product, inventory))
                        .product(product)
                        .build())
                .map(this::sendRequest)
                .collect(Collectors.toSet());
        if(!productsSend.isEmpty()) {
            return AddProductResponse.builder()
                    .status("SUCCESS")
                    .build();
        }
        return AddProductResponse.builder()
                .status("ERROR")
                .build();
    }

    @Override
    public AddProductResponse sendRequest(AddProductRequest request) {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(request.getProductDto());
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        AddProductResponse addProductResponse = mapToResponse(res, AddProductResponse.class);
                        baselinkerLogService.sendSuccesExport(request.getProduct(), addProductResponse);
                        return addProductResponse;
                    })
                    .orElseThrow(() -> new RuntimeException("Empty response"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }
}
