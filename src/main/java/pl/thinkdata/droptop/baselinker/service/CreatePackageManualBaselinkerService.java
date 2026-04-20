package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.createPackageManual.PackageRequest;
import pl.thinkdata.droptop.baselinker.dto.createPackageManual.PackageResponse;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreatePackageManualBaselinkerService  extends BaselinkerWebClientService implements BaselinkerSendable<PackageResponse, PackageRequest> {

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "createPackageManual";
    }

    @Override
    public PackageResponse sendRequest(PackageRequest getOrdersRequest) throws JsonProcessingException {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(getOrdersRequest);
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        return mapToResponse(res, PackageResponse.class);
                    })
                    .orElseThrow(() -> new RuntimeException("Error baselinker api"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }
}

