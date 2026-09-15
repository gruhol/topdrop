package pl.thinkdata.droptop.allegro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeviceAuthorizationResponse(
        @JsonProperty("user_code") String userCode,
        @JsonProperty("device_code") String deviceCode,
        @JsonProperty("verification_uri") String verificationUri,
        @JsonProperty("verification_uri_complete") String verificationUriComplete,
        @JsonProperty("expires_in") String expiresIn,
        @JsonProperty("interval") String interval) {
}
