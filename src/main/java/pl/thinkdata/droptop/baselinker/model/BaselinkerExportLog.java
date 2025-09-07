package pl.thinkdata.droptop.baselinker.model;

import jakarta.persistence.*;
import lombok.*;
import pl.thinkdata.droptop.database.model.Product;

import java.time.LocalDateTime;

@Entity
@Table(name = "baselinker_export_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaselinkerExportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // właściciel relacji
    private Product product;

    private Long baselinkerId;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}