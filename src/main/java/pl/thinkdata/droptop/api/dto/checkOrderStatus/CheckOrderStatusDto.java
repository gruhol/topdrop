package pl.thinkdata.droptop.api.dto.checkOrderStatus;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CheckOrderStatusDto {
    private Long orderNumber;
    private String accountNumber;
    private int transactionNumber;
}