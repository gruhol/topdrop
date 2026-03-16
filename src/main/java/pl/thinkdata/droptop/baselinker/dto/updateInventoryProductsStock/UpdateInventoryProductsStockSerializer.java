package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsStock;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class UpdateInventoryProductsStockSerializer extends JsonSerializer<UpdateInventoryProductsStock> {

    @Override
    public void serialize(UpdateInventoryProductsStock value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("inventory_id", String.valueOf(value.getInventoryId()));

        gen.writeObjectFieldStart("products");
        for (ProductStockUpdate product : value.getProductStockUpdate()) {
            gen.writeObjectFieldStart(String.valueOf(product.getProductId()));
            for (WarehouseStock stock : product.getStocks()) {
                gen.writeNumberField(stock.getWarehouseId(), stock.getStock());
            }
            gen.writeEndObject();
        }
        gen.writeEndObject();

        gen.writeEndObject();
    }
}