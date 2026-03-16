package pl.thinkdata.droptop.database.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.database.model.order.Order;
import pl.thinkdata.droptop.database.repository.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order getOrdersByOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Zamówienie o ID: " + orderId + " nie istnieje"));
    }
}
