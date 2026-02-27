package pl.thinkdata.droptop.database.mapper;

import pl.thinkdata.droptop.baselinker.dto.order.DeliveryInfoBaselinker;
import pl.thinkdata.droptop.baselinker.dto.order.InvoiceInfoBaselinker;
import pl.thinkdata.droptop.baselinker.dto.order.OrderBaselinker;
import pl.thinkdata.droptop.baselinker.dto.order.OrderProductBaselinker;
import pl.thinkdata.droptop.database.model.order.Order;
import pl.thinkdata.droptop.database.model.order.OrderProduct;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class OrderMapper {

    public Order map(OrderBaselinker orderBaselinker) {
        return Order.builder()
                .orderId(orderBaselinker.getOrderId())
                .shopOrderId(orderBaselinker.getShopOrderId())
                .externalOrderId(orderBaselinker.getExternalOrderId())
                .orderSource(orderBaselinker.getOrderSource())
                .orderSourceId(orderBaselinker.getOrderSourceId())
                .orderSourceInfo(orderBaselinker.getOrderSourceInfo())
                .orderStatusId(orderBaselinker.getOrderStatusId())
                .confirmed(orderBaselinker.getConfirmed())
                .dateConfirmed(getLDT(orderBaselinker.getDateConfirmed()))
                .dateAdd(getLDT(orderBaselinker.getDateAdd()))
                .dateInStatus(getLDT(orderBaselinker.getDateInStatus()))
                .userLogin(orderBaselinker.getUserLogin())
                .phone(orderBaselinker.getPhone())
                .email(orderBaselinker.getEmail())
                .userComments(orderBaselinker.getUserComments())
                .adminComments(orderBaselinker.getAdminComments())
                .currency(orderBaselinker.getCurrency())
                .paymentMethod(orderBaselinker.getPaymentMethod())
                .paymentMethodCod(orderBaselinker.getPaymentMethodCod())
                .paymentDone(BigDecimal.valueOf(orderBaselinker.getPaymentDone()))
                .delivery(mapToDelivery(orderBaselinker)) //TODO
                .invoice(mapToInvoice(orderBaselinker)) //TODO
                .extraField1(orderBaselinker.getExtraField1())
                .extraField2(orderBaselinker.getExtraField2())
                .orderPage(orderBaselinker.getOrderPage())
                .pickState(orderBaselinker.getPickState())
                .packState(orderBaselinker.getPackState())
                .star(orderBaselinker.getStar())
                .products(orderBaselinker.getProducts().stream()
                        .map(this::mapToOrderProduct)//TODO
                        .toList())
                .build();
    }

    private OrderProduct mapToOrderProduct(OrderProductBaselinker product) {
        return null;
    }

    private InvoiceInfoBaselinker mapToInvoice(OrderBaselinker orderBaselinker) {
        return null;
    }

    private DeliveryInfoBaselinker mapToDelivery(OrderBaselinker orderBaselinker) {
        return null;
    }

    private LocalDateTime getLDT(Long instant) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(instant), ZoneId.systemDefault());
    }
}
