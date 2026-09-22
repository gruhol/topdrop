package pl.thinkdata.droptop.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.thinkdata.droptop.database.model.product.Product;
import pl.thinkdata.droptop.database.model.product.SyncStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByEan(String keyword, Pageable pageable);

    Page<Product> findAllByTitleContaining(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (:status IS NULL OR p.status = :status) " +
            "AND (:syncStatus IS NULL OR p.syncStatus = :syncStatus)")
    Page<Product> findAllByStatusAndSyncStatus(@Param("status") String status,
                                                @Param("syncStatus") SyncStatus syncStatus,
                                                Pageable pageable);

    @Query("SELECT DISTINCT p.status FROM Product p WHERE p.status IS NOT NULL ORDER BY p.status")
    List<String> findDistinctStatuses();

    Optional<Product> findByEan(String ean);

    List<Product> findByEanIn(List<String> eans);

    List<Product> findTop100BySyncStatusIn(Collection<SyncStatus> statuses);

    List<Product> findTop100ByCategory_IdAndSyncStatusIn(long categoryId, Collection<SyncStatus> statuses);

    List<Product> findTop1000ByExportLogIsNotNullAndSyncStatusIn(Collection<SyncStatus> statuses);

    List<Product> findTop1000ByCategory_IdAndExportLogIsNotNullAndSyncStatusIn(long l, List<SyncStatus> stockUpdate);

    @Query("SELECT p FROM Product p WHERE p.ean IN :eans")
    List<Product> findByEanIn(@Param("eans") Set<String> eans);
}
