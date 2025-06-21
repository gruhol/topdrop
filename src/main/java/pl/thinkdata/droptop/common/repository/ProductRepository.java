package pl.thinkdata.droptop.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.database.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByEan(String keyword, Pageable pageable);

    Page<Product> findAllByTitleContaining(String keyword, Pageable pageable);
}
