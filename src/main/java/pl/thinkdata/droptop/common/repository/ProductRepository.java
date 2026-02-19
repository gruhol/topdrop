package pl.thinkdata.droptop.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.model.SyncStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByEan(String keyword, Pageable pageable);

    Page<Product> findAllByTitleContaining(String keyword, Pageable pageable);

    Optional<Product> findByEan(String ean);

    List<Product> findByEanIn(List<String> eans);

    List<Product> findTop100ByExportLogIsNullAndSyncStatusIn(Collection<SyncStatus> statuses);

    List<Product> findTop100ByExportLogIsNullAndCategory_IdAndSyncStatusIn(long categoryId, Collection<SyncStatus> statuses);

    List<Product> findTop1000ByExportLogIsNotNullAndSyncStatusIn(Collection<SyncStatus> statuses);

    List<Product> findTop1000ByCategory_IdAndExportLogIsNotNullAndSyncStatusIn(long l, List<SyncStatus> stockUpdate);

    @Query("SELECT p FROM Product p WHERE p.ean IN :eans")
    List<Product> findByEanIn(@Param("eans") Set<String> eans);
}
