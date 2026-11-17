package pl.thinkdata.droptop.csvconverter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.thinkdata.droptop.csvconverter.model.CsvClient;
import pl.thinkdata.droptop.csvconverter.repository.CsvClientRepository;
import pl.thinkdata.droptop.csvconverter.service.CsvProcessingService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/csv")
@RequiredArgsConstructor
public class CsvController {

    // Strumieniowe pobieranie: plik źródłowy (>20 MB) nie jest buforowany w pamięci.
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final CsvProcessingService processingService;
    private final CsvClientRepository csvClientRepository;

    @GetMapping(value = "/download/{hash}", produces = "text/csv; charset=UTF-8")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable String hash) {

        CsvClient client = csvClientRepository.findByHash(hash)
                .orElseThrow(() -> new RuntimeException("Client not found for hash: " + hash));

        StreamingResponseBody stream = outputStream -> {
            log.info("Downloading CSV from URL: {}", client.getUrl());

            HttpRequest request = HttpRequest.newBuilder(URI.create(client.getUrl()))
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                    .header(HttpHeaders.ACCEPT, "text/plain, application/octet-stream")
                    .timeout(Duration.ofMinutes(3))
                    .GET()
                    .build();

            try {
                HttpResponse<InputStream> response =
                        HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

                try (InputStream body = response.body()) {
                    if (response.statusCode() != 200) {
                        throw new IOException("Unexpected HTTP status " + response.statusCode()
                                + " from " + client.getUrl());
                    }
                    processingService.process(body, outputStream);
                }

                log.info("CSV processing completed for hash: {}", hash);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("CSV download interrupted for hash: " + hash, e);
            } catch (Exception e) {
                log.error("CSV processing failed for hash: {}", hash, e);
                throw new RuntimeException("CSV processing failed: " + e.getMessage(), e);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + client.getLogo() + ".csv\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(stream);
    }
}
