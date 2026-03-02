package pl.thinkdata.droptop.database.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.thinkdata.droptop.database.model.order.Order;
import pl.thinkdata.droptop.database.service.OrderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public String getOrders(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Order> orders = orderService.getOrders(pageable);
        model.addAttribute("orders", orders );
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", orders.getTotalPages());
        return "database/orders";

    }

    @GetMapping("/order/{orderId}")
    public String getOrders(@PathVariable(value = "orderId", required = true) Long orderId, Model model) throws JsonProcessingException {
        Order order = orderService.getOrdersByOrderId(orderId);
        model.addAttribute("order", order );
        return "database/order";
    }

}
