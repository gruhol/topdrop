package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.dto.Product;
import pl.thinkdata.droptop.common.repository.ProductRepository;

import java.util.Optional;

import static pl.thinkdata.droptop.baselinker.mapper.ProductMapper.map;

@Service
@RequiredArgsConstructor
public class AddProductBaselinkerService extends BaselinkerService implements BaselinkerSendable<Product,AddProductResponse> {

    private final ProductRepository productRepository;

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "addProduct";
    }

    public AddProductResponse sendProduct(String ean) {

        Optional<Product> product = productRepository.findByEan(ean)
                .map(p -> map(p, "platon"));

        if (product.isEmpty()) throw new IllegalArgumentException("Nie ma takiego projektu");
        return sendRequest(product.get());
    }

    @Override
    public AddProductResponse sendRequest(Product product) {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(product);
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> deserialize(res.getBody(), AddProductResponse.class))
                    .orElseThrow(() -> new RuntimeException("Deserialization Error"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }
}
