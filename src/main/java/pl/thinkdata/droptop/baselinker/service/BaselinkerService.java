package pl.thinkdata.droptop.baselinker.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

public class BaselinkerService {

    @Value("${baselinker.token}")
    private String token;

    private String urlApi = "https://api.baselinker.com";

    protected String methodName;

    protected WebClient webClient;

    @PostConstruct
    private void initWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(urlApi)
                .defaultHeader("X-BLToken", token)
                .build();
    }

    ResponseEntity<String> getDataFromWebClient(String json) {
        return webClient.post()
                .uri("/connector.php")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("method=" + methodName + "&parameters=" + json)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}
