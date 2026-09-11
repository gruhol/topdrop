package pl.thinkdata.droptop.allegro.dto;

import java.math.BigDecimal;

public record FeePreviewRequest(Offer offer) {

    public static FeePreviewRequest of(String categoryId, BigDecimal price) {
        return new FeePreviewRequest(new Offer(
                new Category(categoryId),
                new SellingMode("BUY_NOW", new Money(price.toPlainString(), "PLN"))));
    }

    public record Offer(Category category, SellingMode sellingMode) {
    }

    public record Category(String id) {
    }

    public record SellingMode(String format, Money price) {
    }

    public record Money(String amount, String currency) {
    }
}
