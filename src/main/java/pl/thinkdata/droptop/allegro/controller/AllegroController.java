package pl.thinkdata.droptop.allegro.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.thinkdata.droptop.allegro.dto.AllegroPriceResult;
import pl.thinkdata.droptop.allegro.service.AllegroPriceCalculator;

import java.math.BigDecimal;

@RestController
@RequestMapping("/allegro")
@RequiredArgsConstructor
public class AllegroController {

    private final AllegroPriceCalculator priceCalculator;

    @GetMapping("/calculate-price")
    public AllegroPriceResult calculatePrice(@RequestParam String ean, @RequestParam BigDecimal price) {
        return priceCalculator.calculatePriceWithCommission(ean, price);
    }
}
