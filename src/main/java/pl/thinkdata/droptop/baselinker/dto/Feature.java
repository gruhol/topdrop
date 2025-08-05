package pl.thinkdata.droptop.baselinker.dto;

import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Feature {
    public String name;
    public String value;
}

