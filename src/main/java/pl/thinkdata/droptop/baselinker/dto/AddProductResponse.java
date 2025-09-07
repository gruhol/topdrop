package pl.thinkdata.droptop.baselinker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AddProductResponse {
    private String status;
    @JsonProperty("product_id")
    private long productId;
    private Map<String, String> warnings;
}

