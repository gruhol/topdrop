package pl.thinkdata.droptop.baselinker.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.thinkdata.droptop.allegro.service.AllegroPriceCalculator;
import pl.thinkdata.droptop.config.service.SystemSettingService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Slf4j
@RequiredArgsConstructor
public class PriceCalculator {

    private final SystemSettingService systemSettingService;
    private final AllegroPriceCalculator allegroPriceCalculator;

    private BigDecimal packingCost;
    private BigDecimal baselinkerOrderCost;
    private BigDecimal global_margin;

    @PostConstruct
    void init() {
        packingCost = BigDecimal.valueOf(systemSettingService.getValue("packing_cost", Double.class));
        baselinkerOrderCost = BigDecimal.valueOf(systemSettingService.getValue("baselinker_markup_per_order", Double.class));
        global_margin = BigDecimal.valueOf(systemSettingService.getValue("global_margin", Double.class));
    }

    public BigDecimal calculateWholesalesPrice(String ean, double nettPrice, double grossPrice) {
        BigDecimal nett = BigDecimal.valueOf(nettPrice);

        // stawka VAT wyliczona z pary nett/gross, więc działa dla 23%, 8%, 5%...
        BigDecimal vatMultiplier = BigDecimal.valueOf(grossPrice)
                .divide(nett, 6, RoundingMode.HALF_UP);

        // baza netto = cena × (1 + marża) + koszt zamówienia + pakowanie
        BigDecimal baseNett = nett
                .multiply(BigDecimal.ONE.add(global_margin))
                .add(baselinkerOrderCost)
                .add(packingCost);

        BigDecimal baseGross = baseNett.multiply(vatMultiplier);

        return allegroPriceCalculator.calculatePriceWithCommission(ean, baseGross)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
