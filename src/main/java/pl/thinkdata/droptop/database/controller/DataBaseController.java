package pl.thinkdata.droptop.database.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.thinkdata.droptop.database.model.ImportProductRaport;
import pl.thinkdata.droptop.database.model.ImportTypeEnu;
import pl.thinkdata.droptop.database.service.ImportRaportService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class DataBaseController {

    private final ImportRaportService importRaportService;

    @GetMapping("/import-product-raport")
    String getProductRaport(Model model) {
        List<ImportProductRaport> raports = importRaportService.getImportRaport();
        LocalDateTime lastUpdateProduct = raports.stream()
                .filter(importType -> importType.getImportType().equals(ImportTypeEnu.PRODUCT))
                .map(ImportProductRaport::getImportDate)
                .findFirst()
                .orElse(LocalDateTime.of(1999, 01, 01, 00, 00));

        LocalDateTime lastUpdateStock = raports.stream()
                .filter(importType -> importType.getImportType().equals(ImportTypeEnu.STOCK))
                .map(ImportProductRaport::getImportDate)
                .findFirst()
                .orElse(LocalDateTime.of(1999, 01, 01, 00, 00));

        model.addAttribute("raports", raports);
        model.addAttribute("lastUpdateProduct", lastUpdateProduct);
        model.addAttribute("lastUpdateStock", lastUpdateStock);
        return "database/import-raport";
    }
}
