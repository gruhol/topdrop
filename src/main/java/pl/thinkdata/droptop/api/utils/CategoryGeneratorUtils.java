package pl.thinkdata.droptop.api.utils;

import pl.thinkdata.droptop.api.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.api.model.Category;

public interface CategoryGeneratorUtils {
    Category parseStringToCategory(ProductFromXml categoryPath, String productType);
}
