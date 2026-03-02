package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.EmptyRequest;
import pl.thinkdata.droptop.baselinker.dto.GetPriceGroupsResponse;
import pl.thinkdata.droptop.baselinker.dto.Inventory;
import pl.thinkdata.droptop.baselinker.dto.PriceGroupBaseLinker;
import pl.thinkdata.droptop.baselinker.dto.order.GetOrdersRequest;
import pl.thinkdata.droptop.baselinker.dto.order.GetOrdersResponse;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.PriceGroup;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.ProductPriceUpdate;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.UpdateInventoryProductsPrice;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.UpdateInventoryProductsPriceRequest;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.*;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.mapper.OrderMapper;
import pl.thinkdata.droptop.database.model.order.Order;
import pl.thinkdata.droptop.database.model.product.Product;
import pl.thinkdata.droptop.database.model.product.SyncStatus;
import pl.thinkdata.droptop.database.repository.OrderRepository;

import java.util.Collections;
import java.util.List;

import static pl.thinkdata.droptop.database.model.product.SyncStatus.PRICE_STOCK_UPDATE;
import static pl.thinkdata.droptop.database.model.product.SyncStatus.PRICE_UPDATE;

@Service
@RequiredArgsConstructor
public class BaselinkerService {

    public static final String HURTOWA = "hurtowa";

    private final GetInventoryBaselinkerService getInventoryService;
    private final GetPriceGroupsBaselinkerService getPriceGroupsBaselinkerService;
    private final ProductRepository productRepository;
    private final UpdateInventoryProductsPricesBaselinkerService updateInventoryProductsPricesBaselinkerService;
    private final UpdateInventoryProductsStockBaselinkerService updateInventoryProductsStockBaselinkerService;
    private final GetOrdersBaselinkerService getOrdersBaselinkerService;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;


    public UpdateInventoryProductsStockAndPriceResponse sendPriceUpdate() {
        //List<Product> toSyncProducts = productRepository.findTop1000ByExportLogIsNotNullAndSyncStatusIn(List.of(SyncStatus.STOCK_UPDATE));
        List<Product> toSyncProducts = productRepository.findTop1000ByCategory_IdAndExportLogIsNotNullAndSyncStatusIn(144L, List.of(PRICE_UPDATE, PRICE_STOCK_UPDATE));
        if (toSyncProducts.isEmpty()) {
            return UpdateInventoryProductsStockAndPriceResponse.builder()
                    .counter(0)
                    .build();
        }
        UpdateInventoryProductsPriceRequest request = new UpdateInventoryProductsPriceRequest();
        Inventory inventory = getInventoryService.getDefaultInventory();
        GetPriceGroupsResponse priceGroups = getPriceGroupsBaselinkerService.sendRequest(new EmptyRequest());
        request.setProducts(toSyncProducts.stream()
                .map(Product::getEan)
                .toList());
        request.setRequest(UpdateInventoryProductsPrice.builder()
                .inventoryId(inventory.getInventoryId())
                .productPriceUpdate(toSyncProducts.stream()
                        .map(product -> mapToProductPriceUpdate(product, priceGroups))
                        .toList())
                .build());
        return updateInventoryProductsPricesBaselinkerService.sendRequest(request);
    }

    public UpdateInventoryProductsStockAndPriceResponse sendStockUpdate() {
        //List<Product> toSyncProducts = productRepository.findTop1000ByExportLogIsNotNullAndSyncStatusIn(List.of(SyncStatus.STOCK_UPDATE));
        List<Product> toSyncProducts = productRepository.findTop1000ByCategory_IdAndExportLogIsNotNullAndSyncStatusIn(144L, List.of(SyncStatus.STOCK_UPDATE));
        if (toSyncProducts.isEmpty()) {
            return UpdateInventoryProductsStockAndPriceResponse.builder()
                    .counter(0)
                    .build();
        }
        UpdateInventoryProductsStockRequest request = new UpdateInventoryProductsStockRequest();
        Inventory inventory = getInventoryService.getDefaultInventory();
        request.setProducts(toSyncProducts.stream()
                .map(Product::getEan)
                .toList());
        request.setRequest(UpdateInventoryProductsStock.builder()
                .inventoryId(inventory.getInventoryId())
                .productStockUpdate(toSyncProducts.stream()
                        .map(product -> mapToProductStockUpdate(product, inventory))
                        .toList())
                .build());
        return updateInventoryProductsStockBaselinkerService.sendRequest(request);
    }

    public List<Order> getOrders() throws JsonProcessingException {
        GetOrdersResponse getOrdersResponse = getOrdersBaselinkerService.sendRequest(new GetOrdersRequest());
        if (getOrdersResponse.getStatus().equals("SUCCESS")) {
            List<Order> orders = getOrdersResponse.getOrders().stream()
                    .map(orderMapper::map)
                    .filter(order -> orderRepository.findByOrderId(order.getOrderId()).isEmpty())
                    .toList();
            return orderRepository.saveAll(orders);
        }
        return Collections.emptyList();
    }

    private ProductPriceUpdate mapToProductPriceUpdate(Product product, GetPriceGroupsResponse priceGroups) {
        return ProductPriceUpdate.builder()
                .productId(product.getExportLog().getBaselinkerId())
                .price(List.of(PriceGroup.builder()
                        .priceGroupId(priceGroups.getPriceGroups().stream()
                                .filter(name -> name.getName().equals(HURTOWA))
                                .map(PriceGroupBaseLinker::getPriceGroupId)
                                .findFirst().orElse(0L))
                        .price(product.getLatestOffer().getWholesaleGrossPrice())
                        .build()))
                .build();
    }

    private ProductStockUpdate mapToProductStockUpdate(Product product, Inventory inventory) {
        return ProductStockUpdate.builder()
                .productId(product.getExportLog().getBaselinkerId())
                .stocks(List.of(WarehouseStock.builder()
                        .warehouseId(inventory.getDefaultWarehouse())
                        .stock(product.getLatestOffer().getStock())
                        .build()))
                .build();
    }
}
