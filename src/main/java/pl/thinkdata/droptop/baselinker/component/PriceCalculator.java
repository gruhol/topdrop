package pl.thinkdata.droptop.baselinker.component;

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

    public BigDecimal calculateWholesalesPrice(String ean, double nettPrice, double grossPrice) {
        // ustawienia czytane przy każdym wyliczeniu, żeby zmiana w panelu działała bez restartu
        BigDecimal packingCost = getSetting(SystemSettingService.PACKING_COST);
        BigDecimal baselinkerOrderCost = getSetting(SystemSettingService.BASELINKER_MARKUP_PER_ORDER);
        BigDecimal globalMargin = getSetting(SystemSettingService.GLOBAL_MARGIN);

        BigDecimal nett = BigDecimal.valueOf(nettPrice);

        // stawka VAT wyliczona z pary nett/gross, więc działa dla 23%, 8%, 5%...
        BigDecimal vatMultiplier = BigDecimal.valueOf(grossPrice)
                .divide(nett, 6, RoundingMode.HALF_UP);

        // baza netto = cena × (1 + marża) + koszt zamówienia + pakowanie
        BigDecimal baseNett = nett
                .multiply(BigDecimal.ONE.add(globalMargin))
                .add(baselinkerOrderCost)
                .add(packingCost);

        BigDecimal baseGross = baseNett.multiply(vatMultiplier);

        return allegroPriceCalculator.calculatePriceWithCommission(ean, baseGross)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getSetting(String key) {
        return BigDecimal.valueOf(systemSettingService.getValue(key, Double.class));
    }
}
