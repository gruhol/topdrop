package pl.thinkdata.droptop.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateProductInfo {
    private int newprod;
    private int update;
}
