package pl.thinkdata.droptop.baselinker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CategoryBaseLinker {
    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("parent_id")
    private Long parentId;
}
