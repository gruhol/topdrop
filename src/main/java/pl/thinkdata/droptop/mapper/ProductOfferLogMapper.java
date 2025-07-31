package pl.thinkdata.droptop.mapper;

import pl.thinkdata.droptop.database.model.ProductOfferLog;
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
                .stock(convertToStock(record.getQuantity()))
                .fetchedAt(LocalDateTime.now())
                .build();
    }

    private static Integer convertToStock(String quantity) {
        return switch (quantity) {
            case "201-250" -> 201;
            case "251-300" -> 251;
            case "301-500" -> 301;
            case "501-1000" -> 501;
            case ">1000" -> 1001;
            default -> Integer.parseInt(quantity);
        };
    }


}
