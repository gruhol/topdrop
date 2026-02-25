package pl.thinkdata.droptop.baselinker.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectData {

    @JsonProperty("connect_integration_id")
    private Long connectIntegrationId;

    @JsonProperty("connect_contractor_id")
    private Long connectContractorId;
}
