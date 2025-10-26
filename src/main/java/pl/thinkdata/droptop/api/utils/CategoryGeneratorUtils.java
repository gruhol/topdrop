package pl.thinkdata.droptop.api.utils;

import pl.thinkdata.droptop.api.model.Category;

public interface CategoryGeneratorUtils {
    Category parseStringToCategory(String categoryPath, String productType);
}
