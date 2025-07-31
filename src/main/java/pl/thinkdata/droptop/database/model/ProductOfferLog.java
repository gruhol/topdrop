package pl.thinkdata.droptop.database.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_offer_log")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductOfferLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_ean", nullable = false)
    private String productEan;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @Column(name = "wholesale_net_price")
    private Double wholesaleNetPrice;

    @Column(name = "wholesale_gross_price")
    private Double wholesaleGrossPrice;

    @Column(name = "discount_percent")
    private Double discountPercent;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_ean", referencedColumnName = "ean", nullable = false, insertable = false, updatable = false)
    private Product product;
}
