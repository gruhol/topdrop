package pl.thinkdata.droptop.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.thinkdata.droptop.api.dto.catalog.Catalog;
import pl.thinkdata.droptop.api.dto.documentOrderResponse.DocumentOrderResponse;
import pl.thinkdata.droptop.api.dto.stock.Stock;

@Getter
@Setter
@Builder
public class PlatonResponse {
    private String message;
    private Stock stock;
    private Catalog catalog;
    private DocumentOrderResponse documentOrderResponse;
}
