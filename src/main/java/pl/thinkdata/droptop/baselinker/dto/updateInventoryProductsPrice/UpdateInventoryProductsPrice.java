package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@JsonSerialize(using = UpdateInventoryProductsPriceSerializer.class)
public class UpdateInventoryProductsPrice {

    @JsonProperty("inventory_id")
    private Long inventoryId;
    @JsonProperty("products")
    private List<ProductPriceUpdate> productPriceUpdate;
}
