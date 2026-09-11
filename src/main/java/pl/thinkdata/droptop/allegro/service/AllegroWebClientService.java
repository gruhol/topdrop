package pl.thinkdata.droptop.allegro.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

public class AllegroWebClientService {

    private static final String ALLEGRO_MEDIA_TYPE = "application/vnd.allegro.public.v1+json";

    @Value("${allegro.api-url:https://api.allegro.pl}")
    private String apiUrl;

    protected WebClient webClient;

    @PostConstruct
    private void initWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.ACCEPT, ALLEGRO_MEDIA_TYPE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, ALLEGRO_MEDIA_TYPE)
                .build();
    }
}
