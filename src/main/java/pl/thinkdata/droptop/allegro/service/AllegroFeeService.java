package pl.thinkdata.droptop.allegro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.allegro.dto.FeePreviewRequest;
import pl.thinkdata.droptop.allegro.dto.FeePreviewResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AllegroFeeService extends AllegroWebClientService {

    private final AllegroAuthService authService;

    /**
     * Suma prowizji Allegro dla danej kategorii i ceny sprzedaży brutto.
     */
    public BigDecimal getCommission(String categoryId, BigDecimal price) {
        FeePreviewResponse response = webClient.post()
                .uri("/pricing/offer-fee-preview")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                .bodyValue(FeePreviewRequest.of(categoryId, price))
                .retrieve()
                .bodyToMono(FeePreviewResponse.class)
                .block();

        FeePreviewResponse.Quote quote = Optional.ofNullable(response)
                .map(FeePreviewResponse::quotes)
                .flatMap(quotes -> quotes.stream().findFirst())
                .orElseThrow(() -> new RuntimeException(
                        "Błąd pobrania prowizji Allegro dla kategorii: " + categoryId));

        List<FeePreviewResponse.Fee> allFees = Stream.of(quote.fees(), quote.commissions())
                .filter(list -> list != null)
                .flatMap(List::stream)
                .toList();

        allFees.forEach(fee -> log.info("Opłata Allegro [{}] {}: {} {}",
                fee.type(), fee.name(), fee.value().amount(), fee.value().currency()));

        return allFees.stream()
                .map(fee -> fee.value().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
