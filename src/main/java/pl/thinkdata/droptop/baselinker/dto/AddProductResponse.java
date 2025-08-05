package pl.thinkdata.droptop.baselinker.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AddProductResponse {
    private String status;
    private String storage_id;
    private long product_id;
    private Map<String, String> warnings;
}

