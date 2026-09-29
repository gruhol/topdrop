package pl.thinkdata.droptop.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.thinkdata.droptop.database.model.ProductOfferLog;

import java.util.List;
import java.util.Set;

public interface ProductOfferLogRepository extends JpaRepository<ProductOfferLog, Long> {

    // 2 najnowsze oferty dla każdego EAN (nazwa metody nie nakłada limitu przy @Query)
    @Query(value = """
    SELECT * FROM (
        SELECT o.*, ROW_NUMBER() OVER (PARTITION BY o.product_ean ORDER BY o.fetched_at DESC, o.id DESC) AS rn
        FROM product_offer_log o
        WHERE o.product_ean IN (:eans)
    ) ranked
    WHERE ranked.rn <= 2
    """, nativeQuery = true)
    List<ProductOfferLog> findTop2OffersByEans(@Param("eans") Set<String> eans);
}
