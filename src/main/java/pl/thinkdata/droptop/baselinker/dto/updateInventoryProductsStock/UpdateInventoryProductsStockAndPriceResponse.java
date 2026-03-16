package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateInventoryProductsStockAndPriceResponse {
    private String status;
    private Integer counter;
    private Map<String, Object> warnings;
}
