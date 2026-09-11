package pl.thinkdata.droptop.allegro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.allegro.dto.ProductSearchResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AllegroCategoryService extends AllegroWebClientService {

    private final AllegroAuthService authService;

    public String getCategoryIdByEan(String ean) {
        ProductSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sale/products")
                        .queryParam("phrase", ean)
                        .queryParam("mode", "GTIN")
                        .queryParam("language", "pl-PL")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authService.getAccessToken())
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("<brak treści>")
                                .flatMap(body -> {
                                    log.error("Allegro /sale/products zwróciło {} dla EAN {}: {}",
                                            clientResponse.statusCode(), ean, body);
                                    return Mono.error(new RuntimeException(
                                            "Allegro API error " + clientResponse.statusCode() + ": " + body));
                                }))
                .bodyToMono(ProductSearchResponse.class)
                .block();

        return Optional.ofNullable(response)
                .map(ProductSearchResponse::products)
                .flatMap(products -> products.stream().findFirst())
                .map(ProductSearchResponse.Product::category)
                .map(ProductSearchResponse.Category::id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono produktu na Allegro dla EAN: " + ean));
    }
}
