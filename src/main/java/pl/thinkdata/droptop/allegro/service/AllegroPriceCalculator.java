package pl.thinkdata.droptop.allegro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
@RequiredArgsConstructor
public class AllegroPriceCalculator {

    private static final int MAX_ITERATIONS = 10;

    private final AllegroCategoryService categoryService;
    private final AllegroFeeService feeService;

    /**
     * Prowizja Allegro jest liczona od ceny końcowej, więc samo dodanie prowizji
     * od ceny bazowej podnosi cenę, a z nią prowizję. Iterujemy do momentu,
     * aż cena przestanie się zmieniać (zwykle 2-3 iteracje).
     */
    public BigDecimal calculatePriceWithCommission(String ean, BigDecimal basePrice) {
        String categoryId = categoryService.getCategoryIdByEan(ean);

        BigDecimal base = basePrice.setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = base;
        BigDecimal commission = BigDecimal.ZERO;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            commission = feeService.getCommission(categoryId, finalPrice);
            BigDecimal newPrice = base.add(commission).setScale(2, RoundingMode.HALF_UP);
            log.info("Iteracja {} dla {}: cena={}, prowizja={}, nowa cena={}", i, ean, finalPrice, commission, newPrice);
            if (newPrice.compareTo(finalPrice) == 0) {
                break;
            }
            finalPrice = newPrice;
        }

        return finalPrice;
    }
}
