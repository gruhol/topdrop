package pl.thinkdata.droptop.baselinker.dto.addCategory;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AddCategoryDto {
    private String inventor_id;
    private String category_id;
    private String name;
    private String parent_id;
}
