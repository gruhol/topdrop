package pl.thinkdata.droptop.baselinker.mapper;

import pl.thinkdata.droptop.baselinker.dto.Inventory;
import pl.thinkdata.droptop.baselinker.dto.PriceGroupBaseLinker;
import pl.thinkdata.droptop.baselinker.dto.Product;
import pl.thinkdata.droptop.baselinker.dto.TextFields;
import pl.thinkdata.droptop.baselinker.model.BaselinkerExportLog;
import pl.thinkdata.droptop.database.model.ProductOfferLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductMapper {

    public static Product map(pl.thinkdata.droptop.database.model.Product product, Inventory inventory, List<PriceGroupBaseLinker> priceGroups) {
        String defultPriceGroupId = inventory.getDefaultPriceGroup().toString();
        String wholesalePriceId = priceGroups.stream()
                .filter(g -> g.getName().equals("hurtowa"))
                .map(id -> id.getPriceGroupId().toString())
                .findFirst()
                .orElse(defultPriceGroupId);
        
        String defaultWarehouse = inventory.getDefaultWarehouse();
        String inventoryId = inventory.getInventoryId().toString();

        Map<String, Double> prices = new HashMap<>();
        prices.put(defultPriceGroupId, Optional.ofNullable(product.getPrice())
                .orElse(0.0));
        prices.put(wholesalePriceId, Optional.ofNullable(product.getLatestOffer())
                .map(ProductOfferLog::getWholesaleGrossPrice)
                .orElse(0.0));
        Map<String, Integer> stock = new HashMap<>();
        stock.put(defaultWarehouse, Optional.ofNullable(product.getLatestOffer())
                .map(ProductOfferLog::getStock)
                .orElse(0));
        Map<String, String> locations = new HashMap<>();
        locations.put(defaultWarehouse, "platon");
        Map<String, String> images = new HashMap<>();
        images.put("0", "url:" + product.getImg());

        Long idProductToUpdate = Optional.ofNullable(product.getExportLog())
                .map(BaselinkerExportLog::getBaselinkerId)
                .orElse(null);

        return Product.builder()
                .product_id(idProductToUpdate)
                .inventory_id(inventoryId)
                .ean(product.getEan())
                .sku(product.getEan())
                .tax_rate(product.getVat())
                .weight((double) product.getWeight() / 1000)
                .height((double) product.getHeight() / 10)
                .width((double) product.getWidth() / 10)
                .length(product.getDepth() / 10)
                .average_cost(Optional.ofNullable(product.getLatestOffer())
                        .map(ProductOfferLog::getWholesaleGrossPrice)
                        .orElse(0.0))
                //.manufacturer_id("2494877")
                .category_id(product.getCategory().getBaselinkerId())
                .prices(prices)
                .stock(stock)
                .locations(locations)
                .text_fields(createTextFields(product))
                .images(images)
                .build();
    }

    private static TextFields createTextFields(pl.thinkdata.droptop.database.model.Product product) {
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
        reatures.put("publisher", product.getPublisher());
        reatures.put("coverType", product.getCoverType());
        reatures.put("pagesNumber", String.valueOf(product.getPagesNumber()));
        reatures.put("edition", String.valueOf(product.getEdition()));
        reatures.put("approvalNumber", product.getApprovalNumber());
        reatures.put("pcn", product.getPcn());
        reatures.put("manufacturingCountryCode", product.getManufacturingCountryCode());
        reatures.put("gpsr_contractorName", product.getGpsrSekcja().getContractorName());
        reatures.put("gpsr_contractorCountryCode", product.getGpsrSekcja().getContractorCountryCode());
        reatures.put("gpsr_street",  product.getGpsrSekcja().getStreet());
        reatures.put("gpsr_houseNumber",  product.getGpsrSekcja().getHouseNumber());
        reatures.put("gpsr_apartmentNumber", product.getGpsrSekcja().getApartmentNumber());
        reatures.put("gpsr_postalCode",  product.getGpsrSekcja().getPostalCode());
        reatures.put("gpsr_city", product.getGpsrSekcja().getCity());
        reatures.put("gpsr_email", product.getGpsrSekcja().getEmail());
        textFields.setFeatures(reatures);
        return textFields;
    }
}
