package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice;

import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductPriceUpdate {
    private long productId;
    private List<PriceGroup> price;
}
