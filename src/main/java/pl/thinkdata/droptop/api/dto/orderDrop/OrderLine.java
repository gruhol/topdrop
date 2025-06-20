package pl.thinkdata.droptop.api.dto.orderDrop;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderLine {
    private String supplierItemCode;
    private int orderedQuantity;
}
