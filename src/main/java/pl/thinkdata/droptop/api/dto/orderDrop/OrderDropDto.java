package pl.thinkdata.droptop.api.dto.orderDrop;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderDropDto {
    private String orderNumber;
    private String orderDate;
    private String orderRemarks;
    private String accountNumber;
    private DeliveryPoint deliveryPoint;
    private List<OrderLine> orderLine;
    private int transactionNumber;
}
