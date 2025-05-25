package pl.thinkdata.droptop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.thinkdata.droptop.api.ApiExternalService;
import pl.thinkdata.droptop.dto.GetPublicationsDto;
import pl.thinkdata.droptop.dto.GetStocksDto;
import pl.thinkdata.droptop.dto.PlatonResponse;
import pl.thinkdata.droptop.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.mapper.ProductMapper;
import pl.thinkdata.droptop.product.model.Product;
import pl.thinkdata.droptop.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class PlatonApiController {

    private final ApiExternalService apiExternalService;
    private final ProductRepository productRepository;

    @GetMapping("/stany")
    public String getStockFromApi(Model model) {
        GetStocksDto getStocksDto = GetStocksDto.builder()
                .pageNo("1")
                .pageSize("10")
                .lastChangeDate(LocalDateTime.of(2024,01,01, 12, 11, 2, 33))
                .transactionNumber(1)
                .build();
        PlatonResponse data = apiExternalService.createPlatonResponse(getStocksDto);
        model.addAttribute("data", data);

        return "Test";
    }

    @GetMapping("/produkt")
    public String getProductsFromApi(Model model) {
        int pageNumber = 1;
        int downloadCount = 0;
        int total  = 0;
        List<Product> listOfSaveProducts = new ArrayList<>();
        do {
            GetPublicationsDto getPublicationsDto = GetPublicationsDto.builder()
                    .pageNo(String.valueOf(pageNumber))
                    .pageSize("10")
                    //.lastChangeDate(LocalDateTime.of(2022,01,01, 12, 11, 2, 33))
                    .transactionNumber(1)
                    .build();
            PlatonResponse data = apiExternalService.getPublications(getPublicationsDto);
            downloadCount += 10;
            pageNumber++;
            total = data.getCatalog().getSummary().getTotal();
            data.getCatalog().getRc().getProducts().stream()
                    .map(ProductMapper::mapToProduct)
                    .filter(Objects::nonNull)
                    .forEach(product -> {
                            productRepository.save(product);
                            listOfSaveProducts.add(product);
                            });

        }
        while (total > downloadCount);
        model.addAttribute("data", listOfSaveProducts);
        return "Test";
    }


}
