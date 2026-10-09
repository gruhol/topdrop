package pl.thinkdata.droptop.allegro.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.thinkdata.droptop.allegro.dto.FeePreviewRequest;
import pl.thinkdata.droptop.allegro.dto.FeePreviewResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AllegroFeeService extends AllegroWebClientService {

    private final AllegroAuthService authService;

    /**
     * Suma prowizji Allegro dla danej kategorii i ceny sprzedaży brutto.
     */
    public BigDecimal getCommission(String categoryId, BigDecimal price) {
        FeePreviewRequest requestBody = FeePreviewRequest.of(categoryId, price);
        String rawBody;
        try {
            rawBody = webClient.post()
                    .uri("/pricing/offer-fee-preview")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            String requestJson = toJsonQuietly(requestBody);
            log.error("Allegro /pricing/offer-fee-preview zwróciło {} dla żądania {}: {}",
                    e.getStatusCode(), requestJson, e.getResponseBodyAsString());
            throw new RuntimeException("Allegro API error " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        }

        FeePreviewResponse response;
        try {
            response = new ObjectMapper().readValue(rawBody, FeePreviewResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Błąd parsowania odpowiedzi Allegro: " + rawBody, e);
        }

        List<FeePreviewResponse.Fee> commissions = Optional.ofNullable(response)
                .map(FeePreviewResponse::commissions)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new RuntimeException(
                        "Błąd pobrania prowizji Allegro dla kategorii: " + categoryId + ", odpowiedź: " + rawBody));

        return commissions.stream()
                .map(fee -> fee.fee().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static String toJsonQuietly(Object value) {
        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
