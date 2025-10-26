package pl.thinkdata.droptop.baselinker.dto;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AddCategoryResponse {
    private String status;
    private String category_id;
    private String error_code;
    private String error_message;
}
