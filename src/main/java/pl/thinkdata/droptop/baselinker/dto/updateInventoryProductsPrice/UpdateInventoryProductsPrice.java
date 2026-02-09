package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateInventoryProductsPrice {

    @JsonProperty("inventory_id")
    private Long inventoryId;
    @JsonProperty("products")
    private List<ProductPriceUpdate> productStockUpdate;
}
