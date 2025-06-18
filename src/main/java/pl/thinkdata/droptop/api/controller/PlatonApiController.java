package pl.thinkdata.droptop.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.thinkdata.droptop.api.dto.GetPublicationsDto;
import pl.thinkdata.droptop.api.dto.GetStocksDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.service.GetPublicationsExternalService;
import pl.thinkdata.droptop.api.service.GetStocksExternalService;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.ImportRaport;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.repository.ImportRaportRepository;
import pl.thinkdata.droptop.mapper.ProductMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Controller
@RequiredArgsConstructor
public class PlatonApiController {

    private final GetPublicationsExternalService getPublictionService;
    private final GetStocksExternalService getStockService;
    private final ProductRepository productRepository;
    private final ImportRaportRepository importRaportRepository;

    PlatonResponse data;

    @GetMapping("/stany")
    public String getStockFromApi(Model model) {
        GetStocksDto getStocksDto = GetStocksDto.builder()
                .pageNo("1")
                .pageSize("10")
                .lastChangeDate(LocalDateTime.of(2024,01,01, 12, 11, 2, 33))
                .transactionNumber(1)
                .build();
        PlatonResponse data = getStockService.get(getStocksDto);
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
                    .pageSize("100")
                    //.lastChangeDate(LocalDateTime.of(2022,01,01, 12, 11, 2, 33))
                    .transactionNumber(1)
                    .build();
            this.data = getPublictionService.get(getPublicationsDto);

            if (!isNull(data.getMessage())) {
                saveImportRaport("Error", data.getMessage(), total);
                break;
            }
            downloadCount += 100;
            pageNumber++;
            total = data.getCatalog().getSummary().getTotal();
            data.getCatalog().getRc().getProducts().stream()
                    .map(ProductMapper::mapToProduct)
                    .filter(Objects::nonNull)
                    .forEach(listOfSaveProducts::add);
            productRepository.saveAll(listOfSaveProducts);
        }
        while (total > downloadCount);

        if (isNull(this.data.getMessage())) {
            saveImportRaport("OK", null, total);
        }
        model.addAttribute("data", listOfSaveProducts);
        return "Test";
    }

    private void saveImportRaport(String status, String message, int total) {
        ImportRaport importRaport = ImportRaport.builder()
                .importDate(LocalDateTime.now())
                .importRecord(total)
                .importStatus(status)
                .importErrorMessage(message)
                .build();
        importRaportRepository.save(importRaport);
    }
}
