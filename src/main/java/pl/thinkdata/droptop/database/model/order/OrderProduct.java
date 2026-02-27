package pl.thinkdata.droptop.database.model.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_product_id", nullable = false, unique = true)
    private Long orderProductId;

    @Column(name = "storage", length = 50)
    private String storage;

    @Column(name = "storage_id")
    private Long storageId;

    @Column(name = "product_id", length = 100)
    private String productId;

    @Column(name = "variant_id", length = 100)
    private String variantId;

    @Column(name = "name", length = 500)
    private String name;

    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributes;

    @Column(name = "sku", length = 100)
    private String sku;

    @Column(name = "ean", length = 50)
    private String ean;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "warehouse_id")
    private Long warehouseId;

    @Column(name = "auction_id", length = 100)
    private String auctionId;

    @Column(name = "price_brutto", precision = 10, scale = 2)
    private BigDecimal priceBrutto;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "weight", precision = 8, scale = 3)
    private BigDecimal weight;

    @Column(name = "bundle_id")
    private Long bundleId;
}
