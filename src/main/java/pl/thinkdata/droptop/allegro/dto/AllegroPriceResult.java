package pl.thinkdata.droptop.allegro.dto;

import java.math.BigDecimal;

public record AllegroPriceResult(
        String ean,
        String categoryId,
        BigDecimal basePrice,
        BigDecimal commission,
        BigDecimal finalPrice) {
}
