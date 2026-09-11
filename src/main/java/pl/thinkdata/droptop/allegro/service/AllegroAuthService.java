package pl.thinkdata.droptop.allegro.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.thinkdata.droptop.allegro.dto.TokenResponse;

import java.time.Instant;
import java.util.Optional;

@Service
public class AllegroAuthService {

    @Value("${allegro.client-id}")
    private String clientId;

    @Value("${allegro.client-secret}")
    private String clientSecret;

    @Value("${allegro.auth-url:https://allegro.pl/auth/oauth/token}")
    private String authUrl;

    private String accessToken;
    private Instant expiresAt = Instant.EPOCH;

    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt.minusSeconds(60))) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        TokenResponse response = WebClient.create()
                .post()
                .uri(authUrl + "?grant_type=client_credentials")
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
        this.accessToken = Optional.ofNullable(response)
                .map(TokenResponse::accessToken)
                .orElseThrow(() -> new RuntimeException("Błąd pobrania tokenu z Allegro"));
        this.expiresAt = Instant.now().plusSeconds(response.expiresIn());
    }
}
