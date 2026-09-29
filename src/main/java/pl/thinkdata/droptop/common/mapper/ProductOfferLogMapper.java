package pl.thinkdata.droptop.common.mapper;

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
                .stock(convertToStock(record.getQuantity2() != null ? record.getQuantity2() : record.getQuantity()))
                .fetchedAt(LocalDateTime.now())
                .build();
    }

    private static Integer convertToStock(String quantity) {
        if (quantity == null || quantity.isBlank()) {
            return 0;
        }
        return switch (quantity.trim()) {
            case "201-250" -> 201;
            case "251-300" -> 251;
            case "301-500" -> 301;
            case "501-1000" -> 501;
            case ">1000" -> 1001;
            default -> parseStock(quantity.trim());
        };
    }

    private static Integer parseStock(String quantity) {
        try {
            return Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


}
