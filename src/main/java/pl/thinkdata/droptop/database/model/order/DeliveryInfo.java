package pl.thinkdata.droptop.database.model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryInfo {

    @Column(name = "delivery_method_id")
    private Long methodId;

    @Column(name = "delivery_method", length = 100)
    private String method;

    @Column(name = "delivery_price", precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @Column(name = "delivery_package_module", length = 100)
    private String packageModule;

    @Column(name = "delivery_package_nr", length = 100)
    private String packageNr;

    @Column(name = "delivery_fullname", length = 150)
    private String fullname;

    @Column(name = "delivery_company", length = 150)
    private String company;

    @Column(name = "delivery_address", length = 200)
    private String address;

    @Column(name = "delivery_city", length = 100)
    private String city;

    @Column(name = "delivery_state", length = 100)
    private String state;

    @Column(name = "delivery_postcode", length = 20)
    private String postcode;

    @Column(name = "delivery_country_code", length = 10)
    private String countryCode;

    @Column(name = "delivery_country", length = 100)
    private String country;

    @Column(name = "delivery_point_id", length = 100)
    private String pointId;

    @Column(name = "delivery_point_name", length = 100)
    private String pointName;

    @Column(name = "delivery_point_address", length = 200)
    private String pointAddress;

    @Column(name = "delivery_point_postcode", length = 20)
    private String pointPostcode;

    @Column(name = "delivery_point_city", length = 100)
    private String pointCity;
}
