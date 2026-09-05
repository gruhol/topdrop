package pl.thinkdata.droptop.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import pl.thinkdata.droptop.database.dto.analytics.OrderProfitSummary;
import pl.thinkdata.droptop.database.dto.analytics.OrderProfitTotals;
import pl.thinkdata.droptop.database.model.order.Order;

public interface AnalyticsRepository extends Repository<Order, Long> {

    @Query(value = """
        SELECT
            o.order_id                                                      AS numer_zamowienia,
            o.date_add                                                      AS data_zamowienia,
            SUM(op.price_brutto * op.quantity)                              AS przychod_brutto,
            SUM(koszt.cena_zakupu_brutto * op.quantity)                     AS koszt_zakupu_brutto,
            SUM((op.price_brutto - koszt.cena_zakupu_brutto) * op.quantity) AS zysk_brutto,
            SUM((op.price_brutto / (1 + op.tax_rate / 100)
                 - koszt.cena_zakupu_netto) * op.quantity)                  AS zysk_netto,
            SUM(CASE WHEN koszt.cena_zakupu_brutto IS NULL
                     THEN 1 ELSE 0 END)                                     AS pozycje_bez_ceny_zakupu
        FROM orders o
        JOIN order_products op
              ON op.order_id = o.id
        LEFT JOIN LATERAL (
            SELECT l.wholesale_gross_price AS cena_zakupu_brutto,
                   l.wholesale_net_price   AS cena_zakupu_netto
            FROM product_offer_log l
            WHERE l.product_ean = op.ean
              AND l.fetched_at <= o.date_add
            ORDER BY l.wholesale_gross_price ASC, l.fetched_at DESC
            LIMIT 1
        ) koszt ON TRUE
        GROUP BY o.order_id, o.date_add
        ORDER BY o.date_add DESC
        """,
        countQuery = "SELECT COUNT(DISTINCT o.id) FROM orders o JOIN order_products op ON op.order_id = o.id",
        nativeQuery = true)
    Page<OrderProfitSummary> findOrderProfitSummary(Pageable pageable);

    @Query(value = """
        SELECT
            SUM(op.price_brutto * op.quantity)                              AS przychod_brutto,
            SUM(koszt.cena_zakupu_brutto * op.quantity)                     AS koszt_zakupu_brutto,
            SUM((op.price_brutto - koszt.cena_zakupu_brutto) * op.quantity) AS zysk_brutto,
            SUM((op.price_brutto / (1 + op.tax_rate / 100)
                 - koszt.cena_zakupu_netto) * op.quantity)                  AS zysk_netto,
            SUM(CASE WHEN koszt.cena_zakupu_brutto IS NULL
                     THEN 1 ELSE 0 END)                                     AS pozycje_bez_ceny_zakupu
        FROM orders o
        JOIN order_products op
              ON op.order_id = o.id
        LEFT JOIN LATERAL (
            SELECT l.wholesale_gross_price AS cena_zakupu_brutto,
                   l.wholesale_net_price   AS cena_zakupu_netto
            FROM product_offer_log l
            WHERE l.product_ean = op.ean
              AND l.fetched_at <= o.date_add
            ORDER BY l.wholesale_gross_price ASC, l.fetched_at DESC
            LIMIT 1
        ) koszt ON TRUE
        """,
        nativeQuery = true)
    OrderProfitTotals getOrderProfitTotals();
}
