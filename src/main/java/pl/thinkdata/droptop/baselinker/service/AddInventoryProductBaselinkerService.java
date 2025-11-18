package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.AddProductRequest;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.dto.Inventory;
import pl.thinkdata.droptop.baselinker.dto.RequestWithProduct;
import pl.thinkdata.droptop.baselinker.mapper.ProductMapper;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.model.SyncStatus;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddInventoryProductBaselinkerService extends BaselinkerService implements BaselinkerSendable<AddProductResponse, AddProductRequest> {

    private final ProductRepository productRepository;
    private final BaselinkerLogService baselinkerLogService;
    private final GetPriceGroupsBaselinkerService getPriceGroupsService;
    private final GetInventoryBaselinkerService getInventoryService;

    private final List<SyncStatus> newAndUpdateSyncStatus = Arrays.asList(SyncStatus.NEW, SyncStatus.TO_UPDATE);
    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "addInventoryProduct";
    }


    public AddProductResponse sendProduct(String ean) {
        pl.thinkdata.droptop.database.model.Product product = productRepository.findByEan(ean)
                .filter(p -> p.getSyncStatus().equals(SyncStatus.NEW) || p.getSyncStatus().equals(SyncStatus.TO_UPDATE))
                .orElseThrow(() -> new IllegalArgumentException("Nie ma takiego produktu do aktualizacji"));

        Inventory inventory = getInventoryService.getDefaultInventory();

        AddProductRequest request = AddProductRequest.builder()
                .productDto(ProductMapper.map(product, inventory))
                .product(product)
                .build();

        AddProductResponse response = sendRequest(request);

        if (response.getStatus().equals("SUCCESS")) {
            product.setSyncStatus(SyncStatus.SYNCED);
        } else {
            product.setSyncStatus(SyncStatus.ERROR);
        }
        productRepository.save(product);
        return response;
    }

    @Transactional
    public AddProductResponse sendProducts() {
        List<Product> productsToSend = productRepository.findTop100ByExportLogIsNullAndSyncStatusIn(newAndUpdateSyncStatus);
        Inventory inventory = getInventoryService.getDefaultInventory();

        if (productsToSend.isEmpty())
            throw new IllegalArgumentException("Nie ma takich produktów");

        Set<AddProductResponse> productsSend = productsToSend.stream()
                .map(product -> new RequestWithProduct(
                        AddProductRequest.builder()
                                .productDto(ProductMapper.map(product, inventory))
                                .product(product)
                                .build(),
                        product
                ))
                .map(pair -> sendAndChangeStatus(pair.request(), pair.product()))
                .collect(Collectors.toSet());

        if (!productsSend.isEmpty()) {
            return AddProductResponse.builder()
                    .status("SUCCESS")
                    .build();
        }
        return AddProductResponse.builder()
                .status("ERROR")
                .build();
    }

    private AddProductResponse sendAndChangeStatus(AddProductRequest request, Product product) {
        try {
            log.info("Sending product: id={}, ean={}", product.getId(), product.getEan());

            AddProductResponse response = sendRequest(request);
            log.info("Received response for product id={}, status={}", product.getId(), response.getStatus());
            if (response.getStatus().equals("SUCCESS")) {
                product.setSyncStatus(SyncStatus.SYNCED);
            } else {
                product.setSyncStatus(SyncStatus.ERROR);
                log.error("Error syncing product id={}, ean={}, response={}",
                        product.getId(), product.getEan(), response);
            }
            productRepository.save(product);
            return response;
        }  catch (Exception e) {
            log.error("Exception while sending product id={}, ean={} -> {}",
                    product.getId(), product.getEan(), e.getMessage(), e);

            product.setSyncStatus(SyncStatus.ERROR);
            productRepository.save(product);

            return AddProductResponse.builder()
                    .status("ERROR")
                    .build();
        }
    }

    @Override
    public AddProductResponse sendRequest(AddProductRequest request) {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(request.getProductDto());
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        AddProductResponse addProductResponse = mapToResponse(res, AddProductResponse.class);
                        if (!addProductResponse.getStatus().equals("ERROR")) {
                            baselinkerLogService.sendSuccesExport(request.getProduct(), addProductResponse);
                            return addProductResponse;
                        }
                        log.error("Problem to add product id={}, to baselinker error_message={}",
                                addProductResponse.getProductId(), addProductResponse.getError_message());
                        return addProductResponse;
                    })
                    .orElseThrow(() -> new RuntimeException("Empty response"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }
}
