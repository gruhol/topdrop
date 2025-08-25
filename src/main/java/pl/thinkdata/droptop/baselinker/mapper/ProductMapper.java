package pl.thinkdata.droptop.baselinker.mapper;

import pl.thinkdata.droptop.baselinker.dto.Product;
import pl.thinkdata.droptop.baselinker.dto.TextFields;

import java.util.HashMap;
import java.util.Map;

public class ProductMapper {

    public static Product map(pl.thinkdata.droptop.database.model.Product product) {
        Map<String, Double> prices = new HashMap<>();
        prices.put("51936", product.getLatestOffer().getWholesaleGrossPrice());
        Map<String, Integer> stocks = new HashMap<>();
        stocks.put("87714", product.getLatestOffer().getStock());
        Map<String, String> locations = new HashMap<>();
        locations.put("bl_87714", "platon");

        TextFields textFields = new TextFields();
        textFields.setName(product.getTitle());
        textFields.setDescription(product.getDescription());
        Map<String, String> reatures = new HashMap<>();
        reatures.put("releaseDate", product.getReleaseDate());
        reatures.put("status", product.getStatus());
        reatures.put("author", product.getAuthor());
        reatures.put("series", product.getSeries());
        reatures.put("translator", product.getTranslator());
        reatures.put("releaseYear", product.getReleaseYear());
        textFields.setFeatures(reatures);

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
                .locations(locations)
                .text_fields(textFields)
                .build();

    }
}
