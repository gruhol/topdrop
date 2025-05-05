package pl.thinkdata.droptop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.thinkdata.droptop.api.ApiExternalService;
import pl.thinkdata.droptop.dto.GetPublicationsDto;
import pl.thinkdata.droptop.dto.GetStocksDto;
import pl.thinkdata.droptop.dto.PlatonResponse;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class PlatonApiController {

    private final ApiExternalService apiExternalService;

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
        model.addAttribute("data", data);
        return "Test";
    }
}
