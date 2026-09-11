package pl.thinkdata.droptop.allegro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductSearchResponse(List<Product> products) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Product(String id, String name, Category category) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(String id) {
    }
}
