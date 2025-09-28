package pl.thinkdata.droptop.database.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.service.AddInventoryProductBaselinkerService;
import pl.thinkdata.droptop.database.service.ProductService;
import pl.thinkdata.droptop.database.model.Product;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AddInventoryProductBaselinkerService baselinkerService;

    @GetMapping("/produkty")
    public String getAllProduct(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Product> products = productService.getProducts(pageable);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", products.getTotalPages());
        return "database/products";
    }

    @GetMapping("/produkt/send/{ean}")
    public String sendProductToBaseLinker(@PathVariable(value = "ean", required = true) String ean, Model model) {
        String message;
        AddProductResponse result = baselinkerService.sendProduct(ean);
        if (result.getStatus().equals("SUCCESS")) {
            message = "Utworzono o id: " + result.getProductId();
        } else {
            message = "Błąd.";
        }
        model.addAttribute("message", message);
        return "database/alerts/alerts";
    }

    @GetMapping("/category/send")
    public String sendCategoryToBaselinker(Model model) {
        return "";
    }
}
