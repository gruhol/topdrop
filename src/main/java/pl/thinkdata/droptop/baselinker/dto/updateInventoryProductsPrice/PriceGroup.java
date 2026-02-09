package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class PriceGroup {
    private String priceGroupId;
    private double price;
}
