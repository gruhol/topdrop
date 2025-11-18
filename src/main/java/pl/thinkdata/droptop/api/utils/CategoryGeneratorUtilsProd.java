package pl.thinkdata.droptop.api.utils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.api.model.Category;
import pl.thinkdata.droptop.api.repository.CategoryRepository;

@Profile("prod")
@Service
@RequiredArgsConstructor

public class CategoryGeneratorUtilsProd implements CategoryGeneratorUtils {

    private final CategoryRepository categoryRepository;

    @Transactional

    public Category parseStringToCategory(ProductFromXml product, String productType) {

        String[] categoryNames = product.getCategory().split("\\\\");
        Category parent = getParent(productType);
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

    private Category getParent(String productType) {
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
