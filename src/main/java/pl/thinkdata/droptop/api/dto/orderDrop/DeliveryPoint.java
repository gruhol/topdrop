package pl.thinkdata.droptop.api.dto.orderDrop;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class DeliveryPoint {
    private int customerKind;
    private String name;
    private String surname;
    private String street;
    private String homeNumber;
    private String flatNumber;
    private String cityName;
    private String postCode;
    private String post;
    private String email;
    private String phone;
    private String country;
    private int deliveryMethod;
    private String machineName;
}
