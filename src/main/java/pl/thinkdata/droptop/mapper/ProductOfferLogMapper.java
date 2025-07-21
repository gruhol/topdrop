package pl.thinkdata.droptop.mapper;

import pl.thinkdata.droptop.api.dto.stock.ProductOfferLog;
import pl.thinkdata.droptop.api.dto.stock.Record;

import java.time.LocalDateTime;

public class ProductOfferLogMapper {
    public static ProductOfferLog map(Record record, String supplierName) {
        return ProductOfferLog.builder()
                .productEan(record.getEan())
                .supplierId(record.getProductCode())
                .supplierName(supplierName)
                .wholesaleNetPrice(record.getNetPrice())
                .wholesaleGrossPrice(record.getGrossPrice())
                .discountPercent(record.getDiscount())
                .stock(record.getQuantity())
                .fetchedAt(LocalDateTime.now())
                .build();
    }
}
