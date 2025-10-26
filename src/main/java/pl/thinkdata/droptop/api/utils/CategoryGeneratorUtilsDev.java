package pl.thinkdata.droptop.api.utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.model.Category;
import pl.thinkdata.droptop.api.repository.CategoryRepository;

import static java.util.Objects.isNull;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class CategoryGeneratorUtilsDev implements CategoryGeneratorUtils {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category parseStringToCategory(String categoryPath, String productType) {
        Category parent = getParent(productType);
        String[] categoryNames = categoryPath.split("\\\\");

        for (String categoryName : categoryNames) {
            String trimmedName = categoryName.trim();
            if (trimmedName.isEmpty()) {
                continue;
            }

            Category existingCategory = categoryRepository.findByNameAndParent(trimmedName, parent).orElse(null);

            if (existingCategory != null) {
                parent = existingCategory;
            } else {
                parent = categoryRepository.save(Category.builder().name(trimmedName).parent(parent).build());
            }
        }

        return parent;
    }

    /** In Platon database there is sometimes category like:
     * A: Main Category / Subcategory / Subcategory
     * B: Sybcategory / Subcategory
     * method using only for main category "Książki"
     */
    private Category getParent(String productType) {
        if (isNull(productType) || !productType.equals("Książki")) return null;
        return categoryRepository
                .findByNameAndParent(productType, null)
                .orElseGet(() -> {
                    Category mainCat = Category.builder()
                            .name(productType)
                            .parent(null)
                            .build();
                    return categoryRepository.save(mainCat);
                });
    }
}
