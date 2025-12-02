package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateInventoryProductsStockRequest {

    private List<String> products;
    private UpdateInventoryProductsStock request;
}
