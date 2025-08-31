package pl.thinkdata.droptop.baselinker.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Product {
    private String inventory_id;
    private String product_id;
    private String ean;
    private String sku;
    private String tax_rate;
    private double weight;
    private double height;
    private double width;
    private double length;
    private double average_cost;
    private String manufacturer_id;
    private String category_id;
    private Map<String, Double> prices;
    private Map<String, Integer> stock;
    private Map<String, String> locations;
    private TextFields text_fields;
    private Map<Integer, String> images;
}

