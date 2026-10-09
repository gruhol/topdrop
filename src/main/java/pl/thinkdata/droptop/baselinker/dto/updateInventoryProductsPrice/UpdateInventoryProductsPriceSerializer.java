package pl.thinkdata.droptop.baselinker.dto.updateInventoryProductsPrice;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

// Baselinker oczekuje "products": { "<product_id>": { "<price_group_id>": cena } }, a nie listy
public class UpdateInventoryProductsPriceSerializer extends JsonSerializer<UpdateInventoryProductsPrice> {

    @Override
    public void serialize(UpdateInventoryProductsPrice value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("inventory_id", String.valueOf(value.getInventoryId()));

        gen.writeObjectFieldStart("products");
        for (ProductPriceUpdate product : value.getProductPriceUpdate()) {
            gen.writeObjectFieldStart(String.valueOf(product.getProductId()));
            for (PriceGroup priceGroup : product.getPrice()) {
                gen.writeNumberField(String.valueOf(priceGroup.getPriceGroupId()), priceGroup.getPrice());
            }
            gen.writeEndObject();
        }
        gen.writeEndObject();

        gen.writeEndObject();
    }
}
