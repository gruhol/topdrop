package pl.thinkdata.droptop.database.model.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "shop_order_id")
    private Long shopOrderId;

    @Column(name = "external_order_id")
    private String externalOrderId;

    @Column(name = "order_source", length = 50)
    private String orderSource;

    @Column(name = "order_source_id")
    private Long orderSourceId;

    @Column(name = "order_source_info")
    private String orderSourceInfo;

    @Column(name = "order_status_id")
    private Long orderStatusId;

    @Column(name = "platon_order_status", length = 50)
    private String platonOrderStatus;

    @Column(name = "platon_package_number", columnDefinition = "TEXT")
    private String platonPackageNumber;

    @Column(name = "confirmed")
    private boolean confirmed;

    @Column(name = "date_confirmed")
    private LocalDateTime dateConfirmed;

    @Column(name = "date_add")
    private LocalDateTime dateAdd;

    @Column(name = "date_in_status")
    private LocalDateTime dateInStatus;

    @Column(name = "user_login", length = 100)
    private String userLogin;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "user_comments", columnDefinition = "TEXT")
    private String userComments;

    @Column(name = "admin_comments", columnDefinition = "TEXT")
    private String adminComments;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "payment_method_cod", length = 5)
    private String paymentMethodCod;

    @Column(name = "payment_done", precision = 10, scale = 2)
    private BigDecimal paymentDone;

    @Embedded
    private DeliveryInfo delivery;

    @Embedded
    private InvoiceInfo invoice;

    @Column(name = "extra_field_1", columnDefinition = "TEXT")
    private String extraField1;

    @Column(name = "extra_field_2", columnDefinition = "TEXT")
    private String extraField2;

    @Column(name = "order_page", length = 300)
    private String orderPage;

    @Column(name = "pick_state")
    private Integer pickState;

    @Column(name = "pack_state")
    private Integer packState;

    @Column(name = "star")
    private Integer star;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<OrderProduct> products = new ArrayList<>();

    public void addProduct(OrderProduct product) {
        products.add(product);
        product.setOrder(this);
    }

    public void setProducts(List<OrderProduct> products) {
        this.products.clear();
        products.forEach(this::addProduct);
    }
}
