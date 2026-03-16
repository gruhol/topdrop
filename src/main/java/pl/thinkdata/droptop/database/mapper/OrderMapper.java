package pl.thinkdata.droptop.database.mapper;

import org.springframework.stereotype.Component;
import pl.thinkdata.droptop.database.model.order.DeliveryInfo;
import pl.thinkdata.droptop.database.model.order.InvoiceInfo;
import pl.thinkdata.droptop.baselinker.dto.order.OrderBaselinker;
import pl.thinkdata.droptop.baselinker.dto.order.OrderProductBaselinker;
import pl.thinkdata.droptop.database.model.order.Order;
import pl.thinkdata.droptop.database.model.order.OrderProduct;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class OrderMapper {

    public Order map(OrderBaselinker orderBaselinker) {
        Order order = Order.builder()
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
                .delivery(mapToDelivery(orderBaselinker))
                .invoice(mapToInvoice(orderBaselinker))
                .extraField1(orderBaselinker.getExtraField1())
                .extraField2(orderBaselinker.getExtraField2())
                .orderPage(orderBaselinker.getOrderPage())
                .pickState(orderBaselinker.getPickState())
                .packState(orderBaselinker.getPackState())
                .star(orderBaselinker.getStar())
                .build();

        orderBaselinker.getProducts().stream()
                .map(this::mapToOrderProduct)
                .forEach(order::addProduct);
        return order;
    }

    private DeliveryInfo mapToDelivery(OrderBaselinker order) {
        return DeliveryInfo.builder()
                .methodId(order.getDeliveryMethodId())
                .method(order.getDeliveryMethod())
                .price(BigDecimal.valueOf(order.getDeliveryPrice()))
                .packageModule(order.getDeliveryPackageModule())
                .packageNr(order.getDeliveryPackageNr())
                .fullname(order.getDeliveryFullname())
                .company(order.getDeliveryCompany())
                .address(order.getDeliveryAddress())
                .city(order.getDeliveryCity())
                .state(order.getDeliveryState())
                .postcode(order.getDeliveryPostcode())
                .countryCode(order.getDeliveryCountryCode())
                .country(order.getDeliveryCountry())
                .pointId(order.getDeliveryPointId())
                .pointName(order.getDeliveryPointName())
                .pointAddress(order.getDeliveryAddress())
                .pointPostcode(order.getDeliveryPostcode())
                .pointCity(order.getDeliveryPointCity())
                .build();
    }

    private InvoiceInfo mapToInvoice(OrderBaselinker orderBaselinker) {
        return InvoiceInfo.builder()
                .fullname(orderBaselinker.getInvoiceFullname())
                .company(orderBaselinker.getInvoiceCompany())
                .nip(orderBaselinker.getInvoiceNip())
                .address(orderBaselinker.getInvoiceAddress())
                .city(orderBaselinker.getDeliveryCity())
                .state(orderBaselinker.getDeliveryState())
                .postcode(orderBaselinker.getInvoicePostcode())
                .countryCode(orderBaselinker.getInvoiceCountryCode())
                .country(orderBaselinker.getInvoiceCountry())
                .wantInvoice(orderBaselinker.getWantInvoice().equals("1"))
                .build();
    }

    private OrderProduct mapToOrderProduct(OrderProductBaselinker product) {
        return OrderProduct.builder()
                .orderProductId(product.getOrderProductId())
                .storage(product.getStorage())
                .storageId(product.getStorageId())
                .productId(product.getProductId())
                .variantId(product.getVariantId())
                .name(product.getName())
                .attributes(product.getAttributes())
                .sku(product.getSku())
                .ean(product.getEan())
                .location(product.getLocation())
                .warehouseId(product.getWarehouseId())
                .auctionId(product.getAuctionId())
                .priceBrutto(BigDecimal.valueOf(product.getPriceBrutto()))
                .taxRate(BigDecimal.valueOf(product.getTaxRate()))
                .quantity(product.getQuantity())
                .weight(BigDecimal.valueOf(product.getWeight()))
                .bundleId(product.getBundleId())
                .build();
    }

    private LocalDateTime getLDT(Long instant) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(instant), ZoneId.systemDefault());
    }
}
