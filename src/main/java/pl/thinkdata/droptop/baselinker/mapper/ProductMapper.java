package pl.thinkdata.droptop.baselinker.mapper;

import pl.thinkdata.droptop.baselinker.model.Feature;
import pl.thinkdata.droptop.baselinker.model.Product;
import pl.thinkdata.droptop.database.model.ProductOfferLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class ProductMapper {

    public static Product map(pl.thinkdata.droptop.database.model.Product product, String location) {
        List<Feature> features = new ArrayList<>();
        addFeature(features, "releaseDate", product.getReleaseDate());
        addFeature(features, "status", product.getStatus());
        addFeature(features, "author", product.getAuthor());
        addFeature(features, "series", product.getSeries());
        addFeature(features, "translator", product.getTranslator());
        addFeature(features, "releaseYear", product.getReleaseYear());

        return Product.builder()
                .storage_id("bl_1")
                .ean(product.getEan())
                .sku(product.getEan())
                .location(location)
                .name(product.getTitle())
                .quantity(Optional.ofNullable(product.getLatestOffer())
                        .map(ProductOfferLog::getStock)
                        .orElse(0))
                .price_brutto(product.getPrice())
                .price_wholesale_netto(Optional.ofNullable(product.getLatestOffer())
                        .map(ProductOfferLog::getWholesaleNetPrice)
                        .orElse(product.getPrice()))
                .tax_rate(product.getVat())
                .weight(product.getWeight())
                .height(product.getHeight())
                .width(product.getWidth())
                .length(product.getDepth())
                .description(product.getDescription())
                .man_name(product.getPublisher())
                .category_id("4554825")
                //.images(Collections.singletonList(product.getImg()))
                .features(features)
                .build();
    }

    private static void addFeature(List<Feature> features, String name, String value) {
        if (!isNull(value)) {
            features.add(Feature.builder()
                    .name(name)
                    .value(value)
                    .build());
        }
    }

}
