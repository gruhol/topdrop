package pl.thinkdata.droptop.baselinker.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OrderBaselinker {

        @JsonProperty("order_id")
        private Long orderId;

        @JsonProperty("shop_order_id")
        private Long shopOrderId;

        @JsonProperty("external_order_id")
        private String externalOrderId;

        @JsonProperty("order_source")
        private String orderSource;

        @JsonProperty("order_source_id")
        private Long orderSourceId;

        @JsonProperty("order_source_info")
        private String orderSourceInfo;

        @JsonProperty("order_status_id")
        private Long orderStatusId;

        @JsonProperty("date_add")
        private Long dateAdd;

        @JsonProperty("date_confirmed")
        private Long dateConfirmed;

        @JsonProperty("date_in_status")
        private Long dateInStatus;

        @JsonProperty("confirmed")
        private Boolean confirmed;

        @JsonProperty("user_login")
        private String userLogin;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("payment_method")
        private String paymentMethod;

        @JsonProperty("payment_method_cod")
        private String paymentMethodCod;

        @JsonProperty("payment_done")
        private Float paymentDone;

        @JsonProperty("user_comments")
        private String userComments;

        @JsonProperty("admin_comments")
        private String adminComments;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("delivery_method_id")
        private Long deliveryMethodId;

        @JsonProperty("delivery_method")
        private String deliveryMethod;

        @JsonProperty("delivery_price")
        private Float deliveryPrice;

        @JsonProperty("delivery_package_module")
        private String deliveryPackageModule;

        @JsonProperty("delivery_package_nr")
        private String deliveryPackageNr;

        @JsonProperty("delivery_fullname")
        private String deliveryFullname;

        @JsonProperty("delivery_company")
        private String deliveryCompany;

        @JsonProperty("delivery_address")
        private String deliveryAddress;

        @JsonProperty("delivery_postcode")
        private String deliveryPostcode;

        @JsonProperty("delivery_city")
        private String deliveryCity;

        @JsonProperty("delivery_state")
        private String deliveryState;

        @JsonProperty("delivery_country")
        private String deliveryCountry;

        @JsonProperty("delivery_country_code")
        private String deliveryCountryCode;

        @JsonProperty("delivery_point_id")
        private String deliveryPointId;

        @JsonProperty("delivery_point_name")
        private String deliveryPointName;

        @JsonProperty("delivery_point_address")
        private String deliveryPointAddress;

        @JsonProperty("delivery_point_postcode")
        private String deliveryPointPostcode;

        @JsonProperty("delivery_point_city")
        private String deliveryPointCity;

        @JsonProperty("invoice_fullname")
        private String invoiceFullname;

        @JsonProperty("invoice_company")
        private String invoiceCompany;

        @JsonProperty("invoice_nip")
        private String invoiceNip;

        @JsonProperty("invoice_address")
        private String invoiceAddress;

        @JsonProperty("invoice_postcode")
        private String invoicePostcode;

        @JsonProperty("invoice_city")
        private String invoiceCity;

        @JsonProperty("invoice_state")
        private String invoiceState;

        @JsonProperty("invoice_country")
        private String invoiceCountry;

        @JsonProperty("invoice_country_code")
        private String invoiceCountryCode;

        @JsonProperty("want_invoice")
        private String wantInvoice;

        @JsonProperty("extra_field_1")
        private String extraField1;

        @JsonProperty("extra_field_2")
        private String extraField2;

        @JsonProperty("custom_extra_fields")
        private Map<String, Object> customExtraFields;

        @JsonProperty("order_page")
        private String orderPage;

        @JsonProperty("pick_state")
        private Integer pickState;

        @JsonProperty("pack_state")
        private Integer packState;

        @JsonProperty("star")
        private Integer star;

        @JsonProperty("commission")
        private Commission commission;

        @JsonProperty("connect_data")
        private ConnectData connectData;

        @JsonProperty("products")
        private List<OrderProductBaselinker> products;
}
