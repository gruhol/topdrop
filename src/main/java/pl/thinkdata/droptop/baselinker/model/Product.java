package pl.thinkdata.droptop.baselinker.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Product {
    private String storage_id;
    private String product_id;
    private String ean;
    private String sku;
    private String location;
    private String name;
    private int quantity;
    private double price_brutto;
    private double price_wholesale_netto;
    private String tax_rate;
    private double weight;
    private double height;
    private double width;
    private double length;
    private String description;
    private String description_extra1;
    private String description_extra2;
    private String man_name;
    private String category_id;
    private List<String> images;
    private List<Feature> features;
}

