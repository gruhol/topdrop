package pl.thinkdata.droptop.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.thinkdata.droptop.database.model.ProductOfferLog;

import java.util.List;
import java.util.Set;

public interface ProductOfferLogRepository extends JpaRepository<ProductOfferLog, Long> {

    @Query("""
    SELECT o FROM ProductOfferLog o 
    WHERE o.productEan IN :eans 
    ORDER BY o.fetchedAt DESC
    """)
    List<ProductOfferLog> findTop2OffersByEans(@Param("eans") Set<String> eans);
}
