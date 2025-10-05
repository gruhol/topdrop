package pl.thinkdata.droptop.baselinker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetCategoryResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("categories")
    private List<CategoryBaseLinker> categories;
}
