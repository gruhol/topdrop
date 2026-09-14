package pl.thinkdata.droptop.allegro.dto;

import java.math.BigDecimal;
import java.util.List;

public record FeePreviewRequest(Offer offer) {

    public static FeePreviewRequest of(String categoryId, BigDecimal price) {
        return new FeePreviewRequest(new Offer(
                new Category(categoryId),
                List.of(),
                new SellingMode("BUY_NOW", new Money(price.toPlainString(), "PLN")),
                new Publication("ACTIVE")));
    }

    public record Offer(Category category, List<Parameter> parameters, SellingMode sellingMode, Publication publication) {
    }

    public record Category(String id) {
    }

    public record Parameter(String id, List<String> values) {
    }

    public record SellingMode(String format, Money price) {
    }

    public record Money(String amount, String currency) {
    }

    public record Publication(String status) {
    }
}
