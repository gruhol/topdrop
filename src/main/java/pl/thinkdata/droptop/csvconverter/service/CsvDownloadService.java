package pl.thinkdata.droptop.csvconverter.service;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;

@Service
public class CsvDownloadService {

    private final WebClient webClient;

    public CsvDownloadService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void streamCsv(String url, OutputStream outputStream) {

        webClient.get()
                .uri(url)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .doOnNext(dataBuffer -> {
                    try {
                        Channels.newChannel(outputStream)
                                .write(dataBuffer.asByteBuffer());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    } finally {
                        DataBufferUtils.release(dataBuffer);
                    }
                })
                .blockLast();
    }
}
