package pl.thinkdata.droptop.baselinker.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.*;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetInventoryBaselinkerService extends BaselinkerService implements BaselinkerSendable<GetInventoryResponse, EmptyRequest>{

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "getInventories";
    }

    public Inventory getDefaultInventory() {
        GetInventoryResponse response = sendRequest(new EmptyRequest());
        return response.getInventories().stream()
                .filter(Inventory::getIsDefault)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Błąd pobrania głownej kategorii z baselinkera"));
    }

    @Override
    public GetInventoryResponse sendRequest(EmptyRequest request) {

        String jsonParams = "{}";
        ResponseEntity<String> response = getDataFromWebClient(jsonParams);
        return Optional.ofNullable(response)
                .map(res -> mapToResponse(res, GetInventoryResponse.class))
                .orElseThrow(() -> new RuntimeException("Error baselinker api"));
    }
}
