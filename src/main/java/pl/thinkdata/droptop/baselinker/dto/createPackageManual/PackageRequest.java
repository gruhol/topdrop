package pl.thinkdata.droptop.baselinker.dto.createPackageManual;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageRequest {

    @JsonProperty("order_id")
    private long orderId;

    @JsonProperty("courier_code")
    private String courierCode;

    @JsonProperty("package_number")
    private String packageNumber;

    @JsonProperty("pickup_date")
    private long pickupDate;

    @JsonProperty("return_shipment")
    private boolean returnShipment;
}