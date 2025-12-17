package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateInventoryProductsStock {

    @JsonProperty("inventory_id")
    private Long inventoryId;
    @JsonProperty("products")
    private List<ProductStockUpdate> productStockUpdate;
}
