package pl.thinkdata.droptop.baselinker.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductBaselinker {

    @JsonProperty("storage")
    private String storage;

    @JsonProperty("storage_id")
    private Long storageId;

    @JsonProperty("order_product_id")
    private Long orderProductId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("variant_id")
    private String variantId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("ean")
    private String ean;

    @JsonProperty("location")
    private String location;

    @JsonProperty("warehouse_id")
    private Long warehouseId;

    @JsonProperty("auction_id")
    private String auctionId;

    @JsonProperty("attributes")
    private String attributes;

    @JsonProperty("price_brutto")
    private Float priceBrutto;

    @JsonProperty("tax_rate")
    private Float taxRate;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("weight")
    private Float weight;

    @JsonProperty("bundle_id")
    private Long bundleId;
}
