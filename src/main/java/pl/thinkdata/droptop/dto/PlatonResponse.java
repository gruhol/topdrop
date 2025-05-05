package pl.thinkdata.droptop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.thinkdata.droptop.dto.catalog.Catalog;
import pl.thinkdata.droptop.dto.stock.Stock;

@Getter
@Setter
@Builder
public class PlatonResponse {
    private String message;
    private Stock stock;
    private Catalog catalog;
}
