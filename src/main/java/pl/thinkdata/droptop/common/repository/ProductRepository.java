package pl.thinkdata.droptop.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.model.SyncStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByEan(String keyword, Pageable pageable);

    Page<Product> findAllByTitleContaining(String keyword, Pageable pageable);

    Optional<Product> findByEan(String ean);

    List<Product> findByEanIn(List<String> eans);

    List<Product> findTop100ByExportLogIsNullAndSyncStatusIn(Collection<SyncStatus> statuses);
}
