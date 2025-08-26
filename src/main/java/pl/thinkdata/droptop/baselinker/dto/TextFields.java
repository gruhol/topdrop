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
    private String description_extra1;
    private String description_extra2;
    private Map<String, String> features;
}
