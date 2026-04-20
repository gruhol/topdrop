package pl.thinkdata.droptop.baselinker.dto.createPackageManual;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PackageResponse {

    @JsonProperty("package_id")
    private int packageId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("package_number")
    private String packageNumber;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("error_code")
    private String errorCode;
}
