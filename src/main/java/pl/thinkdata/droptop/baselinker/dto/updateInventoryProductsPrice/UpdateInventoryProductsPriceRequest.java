package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice;

import lombok.*;
import pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock.UpdateInventoryProductsStock;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateInventoryProductsPriceRequest {

    private List<String> products;
    private UpdateInventoryProductsStock request;
}
