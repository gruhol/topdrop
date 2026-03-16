package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateInventoryProductsPriceRequest {

    private List<String> products;
    private UpdateInventoryProductsPrice request;
}
