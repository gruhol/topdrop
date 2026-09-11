package pl.thinkdata.droptop.allegro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FeePreviewResponse(List<Quote> quotes) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Quote(String type, List<Fee> fees, List<Fee> commissions) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Fee(String name, String type, Money value) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Money(BigDecimal amount, String currency) {
    }
}
