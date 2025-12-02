package pl.thinkdata.droptop.baselinker.dto.addCategory;

import lombok.*;
import pl.thinkdata.droptop.api.model.Category;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AddCategoryRequest {
    private AddCategoryDto dto;
    private Category category;
}
