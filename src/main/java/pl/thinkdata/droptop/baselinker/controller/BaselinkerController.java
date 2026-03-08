package pl.thinkdata.droptop.baselinker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.dto.addCategory.AddCategoryResponse;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.UpdateInventoryProductsStockAndPriceResponse;
import pl.thinkdata.droptop.baselinker.service.AddCategoryProductBaselinkerService;
import pl.thinkdata.droptop.baselinker.service.AddInventoryProductBaselinkerService;
import pl.thinkdata.droptop.baselinker.service.BaselinkerService;
import pl.thinkdata.droptop.common.exception.NotFoundFileToExportException;
import pl.thinkdata.droptop.database.model.order.Order;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("baselinker")
public class BaselinkerController {

    private final AddInventoryProductBaselinkerService addInventoryProductService;
    private final AddCategoryProductBaselinkerService addCategoryProductService;
    private final BaselinkerService baselinkerService;
    private final ObjectMapper objectMapper;


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
            message.append("Utworzono nowe kategorie.");
        } else {
            message.append("Błąd podczas wysłania kategorii: ");
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
        UpdateInventoryProductsStockAndPriceResponse result = baselinkerService.sendStockUpdate();
        model.addAttribute("message", result.toString());
        return "database/alerts/alerts";
    }

    @GetMapping("/send/price/update")
    public String sendPriceUpdate(Model model) {
        UpdateInventoryProductsStockAndPriceResponse result = baselinkerService.sendPriceUpdate();
        model.addAttribute("message", result.toString());
        return "database/alerts/alerts";
    }

    @GetMapping("/get/orders")
    public String getOrders(Model model) throws JsonProcessingException {
        List<Order> result = baselinkerService.getOrders();
        model.addAttribute("message", "Pobrano: " + result.size() + " nowych zamówień." );
        return "database/alerts/alerts";
    }
}
