package pl.thinkdata.droptop.database.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.thinkdata.droptop.database.service.ProductService;
import pl.thinkdata.droptop.product.model.Product;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/produkty")
    public String getAllProduct(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Product> products = productService.getProducts(pageable);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", products.getTotalPages());
        return "database/products";
    }
}
