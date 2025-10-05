package pl.thinkdata.droptop.baselinker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetInventoryResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("inventories")
    private List<Inventory> inventories;
}
