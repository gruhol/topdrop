package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateInventoryProductsStockResponse {
    private String status;
    private Integer counter;
    private String warnings;
}
