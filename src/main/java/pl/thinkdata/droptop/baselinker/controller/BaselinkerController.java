package pl.thinkdata.droptop.baselinker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.thinkdata.droptop.baselinker.dto.AddCategoryResponse;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.service.AddCategoryProductBaselinkerService;
import pl.thinkdata.droptop.baselinker.service.AddInventoryProductBaselinkerService;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("baselinker")
public class BaselinkerController {

    private final AddInventoryProductBaselinkerService addInventoryProductService;
    private final AddCategoryProductBaselinkerService addCategoryProductService;

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
    public String sendProductsToBaseLinker(Model model) {

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
        String message;
        List<AddCategoryResponse> addCategoryResponse = addCategoryProductService.sendCategories();
        boolean allSuccess = addCategoryResponse.stream()
                .allMatch("SUCCESS"::equals);
        if (allSuccess) {
            message = "Utworzono";
        } else {
            message = "Błąd: ";
        }
        model.addAttribute("message", message);
        return "database/alerts/alerts";
    }
}
