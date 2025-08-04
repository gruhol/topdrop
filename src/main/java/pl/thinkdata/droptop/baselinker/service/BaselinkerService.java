package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.thinkdata.droptop.baselinker.model.Product;
import pl.thinkdata.droptop.common.repository.ProductRepository;

import java.util.Optional;

import static pl.thinkdata.droptop.baselinker.mapper.ProductMapper.map;

@Service
@RequiredArgsConstructor
public class BaselinkerService {

    private final ProductRepository productRepository;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.baselinker.com")
            .defaultHeader("X-BLToken", "xxx")
            .build();

    public boolean sendProduct(String ean) {

        Optional<Product> product = productRepository.findByEan(ean)
                .map(p -> map(p, "platon"));
        try {
            if (product.isEmpty()) return false;
            String jsonParams = new ObjectMapper().writeValueAsString(product.get());
            webClient.post()
                    .uri("/connector.php")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue("method=addProduct&parameters=" + jsonParams)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(System.out::println);
            return true;
        } catch (JsonProcessingException e) {
            System.out.println(e);
            return false;
        }
    }
}
