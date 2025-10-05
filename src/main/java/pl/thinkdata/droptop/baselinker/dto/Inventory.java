package pl.thinkdata.droptop.baselinker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Inventory {
    @JsonProperty("inventory_id")
    private Long inventoryId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("languages")
    private List<String> languages;

    @JsonProperty("default_language")
    private String defaultLanguage;

    @JsonProperty("price_groups")
    private List<Long> priceGroups;

    @JsonProperty("default_price_group")
    private Long defaultPriceGroup;

    @JsonProperty("warehouses")
    private List<String> warehouses;

    @JsonProperty("default_warehouse")
    private String defaultWarehouse;

    @JsonProperty("reservations")
    private Boolean reservations;

    @JsonProperty("is_default")
    private Boolean isDefault;
}
