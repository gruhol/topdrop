package pl.thinkdata.droptop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.thinkdata.droptop.api.ApiExternalService;
import pl.thinkdata.droptop.dto.GetStocksDto;
import pl.thinkdata.droptop.dto.Stock;

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
                .orderNumber(1)
                .build();
        Stock data = apiExternalService.getStock(getStocksDto);
        model.addAttribute("data", data);
        return "Test";
    }
}
