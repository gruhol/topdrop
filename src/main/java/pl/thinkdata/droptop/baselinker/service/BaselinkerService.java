package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.component.PriceCalculator;
import pl.thinkdata.droptop.baselinker.dto.EmptyRequest;
import pl.thinkdata.droptop.baselinker.dto.GetPriceGroupsResponse;
import pl.thinkdata.droptop.baselinker.dto.Inventory;
import pl.thinkdata.droptop.baselinker.dto.PriceGroupBaseLinker;
import pl.thinkdata.droptop.baselinker.dto.createPackageManual.PackageRequest;
import pl.thinkdata.droptop.baselinker.dto.createPackageManual.PackageResponse;
import pl.thinkdata.droptop.baselinker.dto.order.GetOrdersRequest;
import pl.thinkdata.droptop.baselinker.dto.order.GetOrdersResponse;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.PriceGroup;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.ProductPriceUpdate;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.UpdateInventoryProductsPrice;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.UpdateInventoryProductsPriceRequest;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.*;
import pl.thinkdata.droptop.common.repository.ProductOfferLogRepository;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.mapper.OrderMapper;
import pl.thinkdata.droptop.database.model.ProductOfferLog;
import pl.thinkdata.droptop.database.model.order.Order;
import pl.thinkdata.droptop.database.model.product.Product;
import pl.thinkdata.droptop.database.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pl.thinkdata.droptop.database.model.product.SyncStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaselinkerService {

    public static final String HURTOWA = "hurtowa";

    private final GetInventoryBaselinkerService getInventoryService;
    private final GetPriceGroupsBaselinkerService getPriceGroupsBaselinkerService;
    private final ProductRepository productRepository;
    private final ProductOfferLogRepository productOfferLogRepository;
    private final UpdateInventoryProductsPricesBaselinkerService updateInventoryProductsPricesBaselinkerService;
    private final UpdateInventoryProductsStockBaselinkerService updateInventoryProductsStockBaselinkerService;
    private final GetOrdersBaselinkerService getOrdersBaselinkerService;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final CreatePackageManualBaselinkerService createPackageManualBaselinkerService;
    private final PriceCalculator priceCalculator;


    public UpdateInventoryProductsStockAndPriceResponse sendPriceUpdate() {
        List<Product> toSyncProducts = productRepository.findTop1000ByExportLogIsNotNullAndSyncStatusIn(List.of(PRICE_UPDATE, PRICE_STOCK_UPDATE));
        //List<Product> toSyncProducts = productRepository.findTop1000ByCategory_IdAndExportLogIsNotNullAndSyncStatusIn(144L, List.of(PRICE_UPDATE, PRICE_STOCK_UPDATE));
        if (toSyncProducts.isEmpty()) {
            return UpdateInventoryProductsStockAndPriceResponse.builder()
                    .status("EMPTY")
                    .counter(0)
                    .build();
        }

        Inventory inventory = getInventoryService.getDefaultInventory();
        GetPriceGroupsResponse priceGroups = getPriceGroupsBaselinkerService.sendRequest(new EmptyRequest());
        Map<String, ProductOfferLog> latestOffers = getLatestOffersByEan(toSyncProducts);

        List<Product> validProducts = new ArrayList<>();
        List<ProductPriceUpdate> productPriceUpdates = new ArrayList<>();
        for (Product product : toSyncProducts) {
            try {
                productPriceUpdates.add(mapToProductPriceUpdate(product, getOffer(product, latestOffers), priceGroups));
                validProducts.add(product);
            } catch (Exception e) {
                log.error("Exception while calculating price for product id={}, ean={} -> {}",
                        product.getId(), product.getEan(), e.getMessage(), e);
                product.setSyncStatus(ERROR);
                productRepository.save(product);
            }
        }

        if (validProducts.isEmpty()) {
            return UpdateInventoryProductsStockAndPriceResponse.builder()
                    .status("EMPTY")
                    .counter(0)
                    .build();
        }

        UpdateInventoryProductsPriceRequest request = new UpdateInventoryProductsPriceRequest();
        request.setProducts(validProducts.stream()
                .map(Product::getEan)
                .toList());
        request.setRequest(UpdateInventoryProductsPrice.builder()
                .inventoryId(inventory.getInventoryId())
                .productPriceUpdate(productPriceUpdates)
                .build());
        return updateInventoryProductsPricesBaselinkerService.sendRequest(request);
    }

    public UpdateInventoryProductsStockAndPriceResponse sendStockUpdate() {
        List<Product> toSyncProducts = productRepository.findTop1000ByExportLogIsNotNullAndSyncStatusIn(List.of(STOCK_UPDATE));
        //List<Product> toSyncProducts = productRepository.findTop1000ByCategory_IdAndExportLogIsNotNullAndSyncStatusIn(144L, List.of(SyncStatus.STOCK_UPDATE));
        if (toSyncProducts.isEmpty()) {
            return UpdateInventoryProductsStockAndPriceResponse.builder()
                    .status("EMPTY")
                    .counter(0)
                    .build();
        }
        Inventory inventory = getInventoryService.getDefaultInventory();
        Map<String, ProductOfferLog> latestOffers = getLatestOffersByEan(toSyncProducts);

        List<Product> validProducts = new ArrayList<>();
        List<ProductStockUpdate> productStockUpdates = new ArrayList<>();
        for (Product product : toSyncProducts) {
            try {
                productStockUpdates.add(mapToProductStockUpdate(product, getOffer(product, latestOffers), inventory));
                validProducts.add(product);
            } catch (Exception e) {
                log.error("Exception while preparing stock for product id={}, ean={} -> {}",
                        product.getId(), product.getEan(), e.getMessage(), e);
                product.setSyncStatus(ERROR);
                productRepository.save(product);
            }
        }

        if (validProducts.isEmpty()) {
            return UpdateInventoryProductsStockAndPriceResponse.builder()
                    .status("EMPTY")
                    .counter(0)
                    .build();
        }

        UpdateInventoryProductsStockRequest request = new UpdateInventoryProductsStockRequest();
        request.setProducts(validProducts.stream()
                .map(Product::getEan)
                .toList());
        request.setRequest(UpdateInventoryProductsStock.builder()
                .inventoryId(inventory.getInventoryId())
                .productStockUpdate(productStockUpdates)
                .build());
        return updateInventoryProductsStockBaselinkerService.sendRequest(request);
    }

    public List<Order> getOrders() throws JsonProcessingException {
        GetOrdersRequest params = GetOrdersRequest.builder()
                .getUnconfirmedOrders(true)
                .build();
        GetOrdersResponse getOrdersResponse = getOrdersBaselinkerService.sendRequest(params);
        if (getOrdersResponse.getStatus().equals("SUCCESS")) {
            List<Order> orders = getOrdersResponse.getOrders().stream()
                    .map(orderMapper::map)
                    .filter(order -> orderRepository.findByOrderId(order.getOrderId()).isEmpty())
                    .toList();
            return orderRepository.saveAll(orders);
        }
        return Collections.emptyList();
    }

    public PackageResponse sendPackage(Long idOrder) {
        Optional<Order> order = orderRepository.findByOrderId(idOrder);
        if (order.isEmpty()) {
            return PackageResponse.builder()
                    .status("ERROR")
                    .errorCode("Order not found")
                    .errorMessage("Order not found")
                    .build();
        } else {
            List<String> shippingNumbers = order.map(number -> Arrays.asList(number.getPlatonPackageNumber().split(",")))
                    .orElse(Collections.emptyList());
            PackageRequest request = PackageRequest.builder()
                    .orderId(order.get().getOrderId())
                    .courierCode(order.get().getDelivery().getMethod())
                    .packageNumber(shippingNumbers.isEmpty() ? null : shippingNumbers.get(0))
                    .pickupDate(Instant.now().getEpochSecond())
                    .build();
            PackageResponse packageResponse = null;
            try {
                packageResponse = createPackageManualBaselinkerService.sendRequest(request);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return packageResponse;
        }
    }

    // oferty pobierane osobnym zapytaniem, bo Product.offers jest LAZY, a metody wołane są ze schedulera bez sesji
    private Map<String, ProductOfferLog> getLatestOffersByEan(List<Product> products) {
        Set<String> eans = products.stream()
                .map(Product::getEan)
                .collect(Collectors.toSet());
        return productOfferLogRepository.findTop2OffersByEans(eans).stream()
                .collect(Collectors.toMap(ProductOfferLog::getProductEan, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(ProductOfferLog::getFetchedAt,
                                Comparator.nullsFirst(Comparator.naturalOrder())))));
    }

    private ProductOfferLog getOffer(Product product, Map<String, ProductOfferLog> latestOffers) {
        ProductOfferLog offer = latestOffers.get(product.getEan());
        if (offer == null) {
            throw new IllegalStateException("No offer found for ean=" + product.getEan());
        }
        return offer;
    }

    private ProductPriceUpdate mapToProductPriceUpdate(Product product, ProductOfferLog offer, GetPriceGroupsResponse priceGroups) {
        BigDecimal finalPrice = priceCalculator.calculateWholesalesPrice(
                product.getEan(),
                offer.getWholesaleNetPrice(),
                offer.getWholesaleGrossPrice());

        return ProductPriceUpdate.builder()
                .productId(product.getExportLog().getBaselinkerId())
                .price(List.of(PriceGroup.builder()
                        .priceGroupId(priceGroups.getPriceGroups().stream()
                                .filter(name -> name.getName().equals(HURTOWA))
                                .map(PriceGroupBaseLinker::getPriceGroupId)
                                .findFirst().orElse(0L))
                        .price(finalPrice.doubleValue())
                        .build()))
                .build();
    }

    private ProductStockUpdate mapToProductStockUpdate(Product product, ProductOfferLog offer, Inventory inventory) {
        return ProductStockUpdate.builder()
                .productId(product.getExportLog().getBaselinkerId())
                .stocks(List.of(WarehouseStock.builder()
                        .warehouseId(inventory.getDefaultWarehouse())
                        .stock(offer.getStock())
                        .build()))
                .build();
    }
}
