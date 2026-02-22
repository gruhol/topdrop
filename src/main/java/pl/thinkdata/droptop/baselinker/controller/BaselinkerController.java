package pl.thinkdata.droptop.baselinker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.thinkdata.droptop.baselinker.dto.*;
import pl.thinkdata.droptop.baselinker.dto.addCategory.AddCategoryResponse;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.PriceGroup;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.ProductPriceUpdate;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.UpdateInventoryProductsPrice;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice.UpdateInventoryProductsPriceRequest;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.*;
import pl.thinkdata.droptop.baselinker.service.*;
import pl.thinkdata.droptop.common.exception.NotFoundFileToExportException;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.model.SyncStatus;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("baselinker")
public class BaselinkerController {

    public static final String HURTOWA = "hurtowa";
    private final AddInventoryProductBaselinkerService addInventoryProductService;
    private final AddCategoryProductBaselinkerService addCategoryProductService;
    private final UpdateInventoryProductsStockBaselinkerService updateInventoryProductsStockBaselinkerService;
    private final UpdateInventoryProductsPricesBaselinkerService updateInventoryProductsPricesBaselinkerService;
    private final GetPriceGroupsBaselinkerService getPriceGroupsBaselinkerService;
    private final ProductRepository productRepository;
    private final GetInventoryBaselinkerService getInventoryService;

    @GetMapping("/send/product/{ean}")
    public String sendProductToBaseLinker(@PathVariable(value = "ean", required = true) String ean, Model model) {
        String message;
        AddProductResponse result = addInventoryProductService.sendProduct(ean);
        if (result.getStatus().equals("SUCCESS")) {
            message = "Utworzono o id: " + result.getProductId();
        } else {
            message = "Błąd.";
        }
        model.addAttribute("message", message);
        return "database/alerts/alerts";
    }

    @GetMapping("/send/products")
    public String sendProductsToBaseLinker(Model model) throws NotFoundFileToExportException {

        String message;
        AddProductResponse result;
        try {
            result = addInventoryProductService.sendProducts();
            if (result.getStatus().equals("SUCCESS")) {
                message = "Utworzono o id: " + result.getProductId();
            } else {
                message = "Błąd.";
            }
        } catch (NotFoundFileToExportException e) {
            message = "Brak nowych produktów do wysłania";
        }

        model.addAttribute("message", message);
        return "database/alerts/alerts";
    }

    @GetMapping("/send/category")
    public String sendCategoryToBaselinker(Model model) {
        StringBuilder message = new StringBuilder();
        List<AddCategoryResponse> addCategoryResponse = addCategoryProductService.sendCategories();
        boolean allSuccess = addCategoryResponse.stream()
                .map(AddCategoryResponse::getStatus)
                .allMatch("SUCCESS"::equals);
        if (allSuccess) {
            message.append("Utworzono");
        } else {
            message.append("Błąd: ");
            addCategoryResponse.stream()
                    .filter(status -> status.getStatus().equals("ERROR"))
                    .map(toString -> toString.getCategory_id() + " - Error: " + toString.getError_code() + ", Message: " + toString.getError_message())
                    .forEach(message::append);
        }
        model.addAttribute("message", message);
        return "database/alerts/alerts";
    }

    @GetMapping("/send/stocks/update")
    public String sendStockUpdate(Model model) {
        //List<Product> toSyncProducts = productRepository.findTop1000ByExportLogIsNotNullAndSyncStatusIn(List.of(SyncStatus.STOCK_UPDATE));
        List<Product> toSyncProducts = productRepository.findTop1000ByCategory_IdAndExportLogIsNotNullAndSyncStatusIn(143L, List.of(SyncStatus.STOCK_UPDATE));
        if (toSyncProducts.isEmpty()) {
            model.addAttribute("message", "Brak nowych produktów");
            return "database/alerts/alerts";
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
        UpdateInventoryProductsStockAndPriceResponse result = updateInventoryProductsStockBaselinkerService.sendRequest(request);
        model.addAttribute("message", result.toString());
        return "database/alerts/alerts";
    }

    @GetMapping("/send/price/update")
    public String sendPriceUpdate(Model model) {
        //List<Product> toSyncProducts = productRepository.findTop1000ByExportLogIsNotNullAndSyncStatusIn(List.of(SyncStatus.STOCK_UPDATE));
        List<Product> toSyncProducts = productRepository.findTop1000ByCategory_IdAndExportLogIsNotNullAndSyncStatusIn(143L, List.of(SyncStatus.PRICE_UPDATE));
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
        UpdateInventoryProductsStockAndPriceResponse result = updateInventoryProductsPricesBaselinkerService.sendRequest(request);
        model.addAttribute("message", result.toString());
        return "database/alerts/alerts";
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
}
