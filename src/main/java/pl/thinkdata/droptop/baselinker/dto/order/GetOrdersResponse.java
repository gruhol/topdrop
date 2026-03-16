package pl.thinkdata.droptop.baselinker.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class GetOrdersResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("orders")
    private List<OrderBaselinker> orders;
}
