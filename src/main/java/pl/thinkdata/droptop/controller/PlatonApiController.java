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
import pl.thinkdata.droptop.product.model.Product;
import pl.thinkdata.droptop.repository.ProductRepository;

import java.time.LocalDateTime;
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
        GetPublicationsDto getPublicationsDto = GetPublicationsDto.builder()
                .pageNo("1")
                .pageSize("10")
                //.lastChangeDate(LocalDateTime.of(2022,01,01, 12, 11, 2, 33))
                .transactionNumber(1)
                .build();
        PlatonResponse data = apiExternalService.getPublications(getPublicationsDto);
        data.getCatalog().getRc().getProducts().stream()
                .map(this::mapToProduct)
                .filter(Objects::nonNull)
                .forEach(productRepository::save);
        model.addAttribute("data", data);
        return "Test";
    }

    private Product mapToProduct(ProductFromXml product) {
        return Product.builder()
                .ean(product.getEan())
                .isbn(product.getIsbn())
                .title(product.getTitle())
                .releaseDate(product.getReleaseDate())
                .status(product.getStatus())
                .img(product.getImg())
                .author(product.getAuthor())
                .series(product.getSeries())
                .translator(product.getTranslator())
                .category(product.getCategory())
                .publisher(product.getPublisher())
                .description(product.getDescription())
                .releaseYear(product.getReleaseYear())
                .coverType(product.getCoverType())
                .pagesNumber(product.getPagesNumber())
                .width(product.getWidth())
                .height(product.getHeight())
                .edition(product.getEdition())
                .weight(product.getWeight())
                .vat(product.getVat())
                .price(product.getPrice())
                .type(product.getType())
                .depth(product.getDepth())
                .approvalNumber(product.getApprovalNumber())
                .pcn(product.getPcn())
                .manufacturingCountryCode(product.getManufacturingCountryCode())
                .build();
    }
}
