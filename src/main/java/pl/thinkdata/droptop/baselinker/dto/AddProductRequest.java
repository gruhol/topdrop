package pl.thinkdata.droptop.baselinker.dto;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class AddProductRequest {
    private Product productDto;
    private pl.thinkdata.droptop.database.model.Product product;
}
