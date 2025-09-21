package pl.thinkdata.droptop.api.utils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.thinkdata.droptop.api.model.Category;
import pl.thinkdata.droptop.api.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Component
@AllArgsConstructor
public class CategoryGeneratorUtils {

    private CategoryRepository categoryRepository;

    @Transactional
    public Category parseStringToCategory(String categoryPath, String productType) {
        Category isMainCat = categoryRepository
                .findByNameAndParent(productType, null)
                .orElse(null);

        String[] categoryNames = categoryPath.split("\\\\");
        List<Category> categories = new ArrayList<>();
        Category parent = null;

        if(isNull(isMainCat)) {
            Category mainCat = Category.builder()
                    .name(productType)
                    .parent(null)
                    .build();

            parent = categoryRepository.save(mainCat);
        }

        for (String categoryName : categoryNames) {
            String trimmedName = categoryName.trim();
            if (trimmedName.isEmpty()) {
                continue;
            }

            Category existingCategory = categoryRepository
                    .findByNameAndParent(trimmedName, parent)
                    .orElse(null);

            if (existingCategory != null) {
                categories.add(existingCategory);
                parent = existingCategory;
            } else {
                Category newCategory = Category.builder()
                        .name(trimmedName)
                        .parent(parent)
                        .build();

                Category savedCategory = categoryRepository.save(newCategory);
                categories.add(savedCategory);
                parent = savedCategory;
            }
        }

        return parent;
    }
}
