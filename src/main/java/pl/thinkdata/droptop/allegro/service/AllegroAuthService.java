package pl.thinkdata.droptop.allegro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.thinkdata.droptop.allegro.dto.DeviceAuthorizationResponse;
import pl.thinkdata.droptop.allegro.dto.TokenResponse;
import pl.thinkdata.droptop.config.service.SystemSettingService;

import java.time.Instant;
import java.util.Optional;

/**
 * Zasoby Allegro powiązane z kontem sprzedawcy (np. /sale/products) wymagają tokenu
 * z kontekstem użytkownika (bearer-token-for-user) - token aplikacyjny (client_credentials)
 * nie wystarczy, niezależnie od uprawnień aplikacji (potwierdzone przez Allegro:
 * https://github.com/allegro/allegro-api/issues/11004).
 * Dlatego autoryzację robimy raz, ręcznie, przez Device Flow (strona /admin/allegro/connect),
 * a uzyskany refresh_token jest trwale zapisywany w system_settings i używany do automatycznego
 * odświeżania access_token bez dalszego udziału człowieka (refresh_token jest ważny 3 miesiące
 * i odnawia się przy każdym użyciu).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AllegroAuthService {

    private static final String REFRESH_TOKEN_KEY = "allegro_refresh_token";

    @Value("${allegro.client-id}")
    private String clientId;

    @Value("${allegro.client-secret}")
    private String clientSecret;

    @Value("${allegro.auth-url:https://allegro.pl/auth/oauth}")
    private String authUrl;

    private final SystemSettingService systemSettingService;

    private String accessToken;
    private Instant expiresAt = Instant.EPOCH;

    private DeviceAuthorizationResponse pendingAuthorization;

    public synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt.minusSeconds(60))) {
            refreshAccessToken();
        }
        return accessToken;
    }

    public boolean isConnected() {
        return systemSettingService.findValue(REFRESH_TOKEN_KEY).isPresent();
    }

    public synchronized Optional<DeviceAuthorizationResponse> getPendingAuthorization() {
        return Optional.ofNullable(pendingAuthorization);
    }

    public synchronized DeviceAuthorizationResponse startDeviceAuthorization() {
        DeviceAuthorizationResponse response;
        try {
            response = WebClient.create()
                    .post()
                    .uri(authUrl + "/device?client_id=" + clientId)
                    .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .retrieve()
                    .bodyToMono(DeviceAuthorizationResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            String body = e.getResponseBodyAsString();
            log.error("Błąd inicjalizacji Device Flow Allegro {}: {}", e.getStatusCode(), body);
            throw new RuntimeException("Błąd inicjalizacji Device Flow Allegro " + e.getStatusCode() + ": " + body, e);
        }

        this.pendingAuthorization = Optional.ofNullable(response)
                .filter(r -> r.deviceCode() != null)
                .orElseThrow(() -> new RuntimeException("Błąd inicjalizacji Device Flow Allegro: pusta odpowiedź"));
        return response;
    }

    /**
     * @return jeden z: AUTORYZOWANO, OCZEKIWANIE_NA_ZATWIERDZENIE, KOD_WYGASL,
     * ODRZUCONO_PRZEZ_UZYTKOWNIKA, BRAK_OCZEKUJACEJ_AUTORYZACJI, BLAD: ...
     */
    public synchronized String pollDeviceAuthorization() {
        if (pendingAuthorization == null) {
            return "BRAK_OCZEKUJACEJ_AUTORYZACJI";
        }
        try {
            TokenResponse response = WebClient.create()
                    .post()
                    .uri(authUrl + "/token")
                    .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue("grant_type=urn:ietf:params:oauth:grant-type:device_code&device_code="
                            + pendingAuthorization.deviceCode())
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .block();

            applyToken(response);
            pendingAuthorization = null;
            return "AUTORYZOWANO";
        } catch (WebClientResponseException e) {
            String body = e.getResponseBodyAsString();
            if (body.contains("authorization_pending") || body.contains("slow_down")) {
                return "OCZEKIWANIE_NA_ZATWIERDZENIE";
            }
            if (body.contains("expired_token")) {
                pendingAuthorization = null;
                return "KOD_WYGASL";
            }
            if (body.contains("access_denied")) {
                pendingAuthorization = null;
                return "ODRZUCONO_PRZEZ_UZYTKOWNIKA";
            }
            log.error("Błąd polling Device Flow Allegro: {}", body, e);
            pendingAuthorization = null;
            return "BLAD: " + body;
        }
    }

    private void refreshAccessToken() {
        String refreshToken = systemSettingService.findValue(REFRESH_TOKEN_KEY)
                .orElseThrow(() -> new IllegalStateException(
                        "Brak autoryzacji Allegro. Połącz aplikację na stronie /admin/allegro/connect."));

        TokenResponse response = WebClient.create()
                .post()
                .uri(authUrl + "/token")
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue("grant_type=refresh_token&refresh_token=" + refreshToken)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        applyToken(response);
    }

    private void applyToken(TokenResponse response) {
        TokenResponse tokenResponse = Optional.ofNullable(response)
                .orElseThrow(() -> new RuntimeException("Błąd pobrania tokenu z Allegro"));

        this.accessToken = tokenResponse.accessToken();
        this.expiresAt = Instant.now().plusSeconds(tokenResponse.expiresIn());

        if (tokenResponse.refreshToken() != null) {
            systemSettingService.upsertValue(REFRESH_TOKEN_KEY, tokenResponse.refreshToken(),
                    "STRING", "Allegro OAuth refresh token (Device Flow) - nie edytuj ręcznie");
        }
    }
}
