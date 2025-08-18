package pl.thinkdata.droptop.baselinker.dto;

import lombok.*;

import java.util.Map;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TextFields {
    private String name;
    private String description;
    private String descriptionExtra1;
    private String descriptionExtra2;
    private Map<String, String> features;
}
