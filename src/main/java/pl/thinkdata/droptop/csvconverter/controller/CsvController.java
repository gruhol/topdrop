package pl.thinkdata.droptop.csvconverter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pl.thinkdata.droptop.csvconverter.model.CsvClient;
import pl.thinkdata.droptop.csvconverter.repository.CsvClientRepository;
import pl.thinkdata.droptop.csvconverter.service.CsvProcessingService;

import java.io.ByteArrayInputStream;
import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/csv")
@RequiredArgsConstructor
public class CsvController {

    private final CsvProcessingService processingService;
    private final CsvClientRepository csvClientRepository;
    private final WebClient webClient;

    @GetMapping(value = "/download/{hash}", produces = "text/csv; charset=UTF-8")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable String hash) {

        CsvClient client = csvClientRepository.findByHash(hash)
                .orElseThrow(() -> new RuntimeException("Client not found for hash: " + hash));

        StreamingResponseBody stream = outputStream -> {
            try {
                log.info("Downloading CSV from URL: {}", client.getUrl());
                byte[] csvBytes = webClient.get()
                        .uri(client.getUrl())
                        .header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                        .accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM)
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .timeout(Duration.ofMinutes(2))
                        .blockOptional()
                        .orElseThrow(() -> new RuntimeException("Failed to download CSV"));

                log.info("Downloaded {} bytes, processing...", csvBytes.length);

                processingService.process(
                        new ByteArrayInputStream(csvBytes),
                        outputStream
                );

                log.info("CSV processing completed for hash: {}", hash);

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