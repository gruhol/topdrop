package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductStockUpdate {
    private String productId;
    private List<WarehouseStock> stocks;
}
