package pl.thinkdata.droptop.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.thinkdata.droptop.database.model.order.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
