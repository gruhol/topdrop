package pl.thinkdata.droptop.baselinker.dto;

import lombok.*;

import java.util.List;
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
    private String manufacturer_id;
    private String category_id;
    private Map<String, Double> prices;
    private Map<String, Integer> stocks;
    private Map<String, String> locations;
    private TextFields textFields;
    private Map<Integer, String> images;
}

