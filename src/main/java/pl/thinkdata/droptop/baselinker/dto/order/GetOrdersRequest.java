package pl.thinkdata.droptop.baselinker.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetOrdersRequest {

    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("date_confirmed_from")
    private Integer dateConfirmedFrom;

    @JsonProperty("date_from")
    private Integer dateFrom;

    @JsonProperty("id_from")
    private Integer idFrom;

    @JsonProperty("get_unconfirmed_orders")
    @Builder.Default
    private Boolean getUnconfirmedOrders = false;

    @JsonProperty("status_id")
    private Integer statusId;

    @JsonProperty("filter_email")
    private String filterEmail;

    @JsonProperty("filter_order_source")
    private String filterOrderSource;

    @JsonProperty("filter_order_source_id")
    private Integer filterOrderSourceId;

    @JsonProperty("filter_shop_order_id")
    private Integer filterShopOrderId;

    @JsonProperty("include_custom_extra_fields")
    @Builder.Default
    private Boolean includeCustomExtraFields = false;

    @JsonProperty("include_commission_data")
    @Builder.Default
    private Boolean includeCommissionData = false;

    @JsonProperty("include_connect_data")
    @Builder.Default
    private Boolean includeConnectData = false;
}
