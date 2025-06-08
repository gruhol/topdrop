package pl.thinkdata.droptop.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.product.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
