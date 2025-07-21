package pl.thinkdata.droptop.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.api.dto.stock.ProductOfferLog;

public interface ProductOfferLogRepository extends JpaRepository<ProductOfferLog, Long> {
}
