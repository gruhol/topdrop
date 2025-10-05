package pl.thinkdata.droptop.baselinker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class PriceGroupBaseLinker {

    @JsonProperty("price_group_id")
    private Long priceGroupId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("is_default")
    private Boolean isDefault;
}
