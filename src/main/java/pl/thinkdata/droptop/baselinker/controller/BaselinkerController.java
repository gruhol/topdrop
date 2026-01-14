package pl.thinkdata.droptop.baselinker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.thinkdata.droptop.baselinker.dto.*;
import pl.thinkdata.droptop.baselinker.dto.addCategory.AddCategoryResponse;
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

    private final AddInventoryProductBaselinkerService addInventoryProductService;
    private final AddCategoryProductBaselinkerService addCategoryProductService;
    private final UpdateInventoryProductsStockBaselinkerService updateInventoryProductsStockBaselinkerService;
    private final GetPriceGroupsBaselinkerService getPriceGroupsService;
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
        AddProductResponse result = addInventoryProductService.sendProducts();
        if (result.getStatus().equals("SUCCESS")) {
            message = "Utworzono o id: " + result.getProductId();
        } else {
            message = "Błąd.";
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
        List<Product> toSyncProducts = productRepository.findTop1000ByExportLogIsNotNullAndSyncStatusIn(List.of(SyncStatus.STOCK_UPDATE));
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
        UpdateInventoryProductsStockResponse result = updateInventoryProductsStockBaselinkerService.sendRequest(request);
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
}
