package pl.thinkdata.droptop.database.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.thinkdata.droptop.baselinker.service.AddInventoryProductBaselinkerService;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.service.ProductService;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class ProductController {

    private final ProductService productService;
    private final AddInventoryProductBaselinkerService baselinkerService;

    @GetMapping("/products")
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
}
