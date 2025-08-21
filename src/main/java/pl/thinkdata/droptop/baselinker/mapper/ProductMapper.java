package pl.thinkdata.droptop.baselinker.mapper;

import pl.thinkdata.droptop.baselinker.dto.Product;

import java.util.HashMap;
import java.util.Map;

public class ProductMapper {

    public static Product map(pl.thinkdata.droptop.database.model.Product product) {
//        List<Feature> features = new ArrayList<>();
//        addFeature(features, "releaseDate", product.getReleaseDate());
//        addFeature(features, "status", product.getStatus());
//        addFeature(features, "author", product.getAuthor());
//        addFeature(features, "series", product.getSeries());
//        addFeature(features, "translator", product.getTranslator());
//        addFeature(features, "releaseYear", product.getReleaseYear());

//        return Product.builder()
//                .inventory_id("bl_1")
//                .ean(product.getEan())
//                .sku(product.getEan())
//
//                .quantity(Optional.ofNullable(product.getLatestOffer())
//                        .map(ProductOfferLog::getStock)
//                        .orElse(0))
//                .price_brutto(product.getPrice())
//                .price_wholesale_netto(Optional.ofNullable(product.getLatestOffer())
//                        .map(ProductOfferLog::getWholesaleNetPrice)
//                        .orElse(product.getPrice()))
//                .tax_rate(product.getVat())
//                .weight(product.getWeight())
//                .description(product.getDescription())
//                .man_name(product.getPublisher())
//                .category_id("4554825")
//                //.images(Collections.singletonList(product.getImg()))
//                .features(features)
//                .build();
        Map<String, Double> prices = new HashMap<>();
        prices.put("51936", product.getLatestOffer().getWholesaleGrossPrice());
        Map<String, Integer> stocks = new HashMap<>();
        stocks.put("87714", product.getLatestOffer().getStock());

        return Product.builder()
                .inventory_id("59592")
                .ean(product.getEan())
                .sku(product.getEan())
                .tax_rate(product.getVat())
                .weight(product.getWeight())
                .height(product.getHeight())
                .width(product.getWidth())
                .length(product.getDepth())
                .manufacturer_id("2494877")
                .category_id("4554825")
                .prices(prices)
                .stocks(stocks)
                .build();

    }
}
