package pl.thinkdata.droptop.baselinker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetPriceGroupsResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("price_groups")
    private Map<String, PriceGroupBaseLinker> priceGroups;
}
