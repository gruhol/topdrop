package pl.thinkdata.droptop.baselinker.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.*;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetPriceGroupsBaselinkerService extends BaselinkerService implements BaselinkerSendable<GetPriceGroupsResponse, EmptyRequest>{

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "getInventoryPriceGroups";
    }

    public Long getIdDefaultPriceGroup() {
        GetPriceGroupsResponse response = sendRequest(new EmptyRequest());
        return response.getPriceGroups().stream()
                .filter(PriceGroupBaseLinker::getIsDefault)
                .map(PriceGroupBaseLinker::getPriceGroupId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Błąd pobrania głownej kategorii z baselinkera"));
    }

    @Override
    public GetPriceGroupsResponse sendRequest(EmptyRequest request) {

        String jsonParams = "{}";
        ResponseEntity<String> response = getDataFromWebClient(jsonParams);
        return Optional.ofNullable(response)
                .map(res -> mapToResponse(res, GetPriceGroupsResponse.class))
                .orElseThrow(() -> new RuntimeException("Error baselinker api"));
    }
}
