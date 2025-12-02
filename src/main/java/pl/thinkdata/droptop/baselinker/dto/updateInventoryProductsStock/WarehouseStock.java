package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class WarehouseStock {
    private String warehouseId;
    private Integer stock;
}
