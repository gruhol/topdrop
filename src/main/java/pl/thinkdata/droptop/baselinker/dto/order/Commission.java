package pl.thinkdata.droptop.baselinker.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Commission {

    @JsonProperty("net")
    private BigDecimal net;

    @JsonProperty("gross")
    private BigDecimal gross;

    @JsonProperty("currency")
    private String currency;
}