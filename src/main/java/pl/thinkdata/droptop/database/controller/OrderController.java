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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.checkOrderStatus.CheckOrderStatusDto;
import pl.thinkdata.droptop.api.dto.orderDrop.DeliveryPoint;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderDropDto;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderLine;
import pl.thinkdata.droptop.api.service.CheckStockService;
import pl.thinkdata.droptop.api.service.GetCheckOrderStatusExternalService;
import pl.thinkdata.droptop.api.service.GetOrderDropExternalService;
import pl.thinkdata.droptop.baselinker.dto.createPackageManual.PackageResponse;
import pl.thinkdata.droptop.baselinker.service.BaselinkerService;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.dto.AddressNumber;
import pl.thinkdata.droptop.database.model.order.Order;
import pl.thinkdata.droptop.database.model.order.OrderProduct;
import pl.thinkdata.droptop.database.repository.OrderSendLogRepository;
import pl.thinkdata.droptop.database.service.OrderService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class OrderController {

    public static final int PACZKOMAT = 16;
    public static final int KURIER = 42;
    public static final int COMPANY = 0;
    public static final int COSTUMER = 1;
    public static final String ACCOUNT_NUMBER = "30418";
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final CheckStockService checkStockService;
    private final GetOrderDropExternalService getOrderDropExternalService;
    private final GetCheckOrderStatusExternalService getCheckOrderStatusExternalService;
    private final OrderSendLogRepository orderSendLogRepository;
    private final BaselinkerService baselinkerService;

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
        model.addAttribute("order", order);
        model.addAttribute("sendLogs", orderSendLogRepository.findByOrderNumberOrderByRequestDateDesc(orderId));
        return "database/order";
    }

    @GetMapping("order/checkstatus/{orderId}")
    public String checkOrderStatus(@PathVariable(value = "orderId") Long orderId, RedirectAttributes redirectAttributes) {
        CheckOrderStatusDto dto = CheckOrderStatusDto.builder()
                .orderNumber(orderId)
                .accountNumber(ACCOUNT_NUMBER)
                .transactionNumber(orderId.intValue())
                .build();
        PlatonResponse response = getCheckOrderStatusExternalService.get(dto);
        if (response.isError()) {
            redirectAttributes.addFlashAttribute("errorMessage", response.getMessage());
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Sprawdzono status zamówienia");
        }
        return "redirect:/admin/order/" + orderId;
    }

    @GetMapping("order/send/{orderId}")
    public String sendOrder(@PathVariable(value = "orderId") Long orderId, RedirectAttributes redirectAttributes) {
        Order order = orderService.getOrdersByOrderId(orderId);
        List<String> productsOrdered = order.getProducts().stream()
                .map(OrderProduct::getEan)
                .toList();
        List<Long> productInDatabase = productRepository.findByEanIn(productsOrdered).stream()
                .map(lastOffer -> lastOffer.getLatestOffer().getSupplierId())
                .toList();
        if (checkStockService.checkStockFromApi(productInDatabase)) {
            DeliveryPoint deliveryPoint = prepareDeliveryPoint(order);
            List<OrderLine> orderLines = prepareOrderLine(order);

            OrderDropDto orderDropDto = OrderDropDto.builder()
                    .orderNumber(order.getOrderId())
                    .orderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .accountNumber(ACCOUNT_NUMBER)
                    .deliveryPoint(deliveryPoint)
                    .orderLine(orderLines)
                    .build();

            getOrderDropExternalService.get(orderDropDto);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Zamówienie wysłane");
        return "redirect:/admin/order/" + orderId;
    }

    @GetMapping("/package/send/{orderId}")
    public String sendPackage(@PathVariable(value = "orderId") Long orderId, RedirectAttributes redirectAttributes) {
        Order order = orderService.getOrdersByOrderId(orderId);
        PackageResponse packageResponse = baselinkerService.sendPackage(order.getOrderId());
        redirectAttributes.addFlashAttribute("successMessage", "Status wysłania paczki: " + packageResponse.getStatus());
        return "redirect:/admin/order/" + orderId;
    }

    private List<OrderLine> prepareOrderLine(Order order) {
        return order.getProducts().stream().map(product ->
                OrderLine.builder()
                    .supplierItemCode(product.getEan())
                    .orderedQuantity(product.getQuantity())
                    .build()
            ).toList();
    }

    private DeliveryPoint prepareDeliveryPoint(Order order) {
        AddressNumber address = AddressNumber.of(order.getDelivery().getPointAddress());
        int deliveryMethod = isPaczkomat(order.getDelivery().getPointName()) ? PACZKOMAT : KURIER;

        return DeliveryPoint.builder()
                .customerKind(order.getInvoice().isWantInvoice() ? COMPANY : COSTUMER)
                .name(order.getUserLogin())
                .surname(order.getDelivery().getFullname())
                .street(address.street())
                .homeNumber(address.houseNumber())
                .flatNumber(address.flatNumber())
                .cityName(order.getDelivery().getCity())
                .postCode(order.getDelivery().getPostcode())
                .post(order.getDelivery().getCity())
                .email(order.getEmail())
                .phone(order.getPhone())
                .country(order.getDelivery().getCountry())
                .deliveryMethod(deliveryMethod)
                .machineName(order.getDelivery().getPointId())
                .build();
    }

    private boolean isPaczkomat(String input) {
        String firstWord = input.trim().split("\\s+")[0];
        return firstWord.equalsIgnoreCase("Paczkomat");
    }

}
