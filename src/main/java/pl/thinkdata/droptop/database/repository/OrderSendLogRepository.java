package pl.thinkdata.droptop.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.thinkdata.droptop.database.model.order.OrderSendLog;

import java.util.List;

@Repository
public interface OrderSendLogRepository extends JpaRepository<OrderSendLog, Long> {
    List<OrderSendLog> findByOrderNumber(Long orderNumber);
}
