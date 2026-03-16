package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.order.GetOrdersRequest;
import pl.thinkdata.droptop.baselinker.dto.order.GetOrdersResponse;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetOrdersBaselinkerService extends BaselinkerWebClientService implements BaselinkerSendable<GetOrdersResponse, GetOrdersRequest> {

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "getOrders";
    }

    @Override
    public GetOrdersResponse sendRequest(GetOrdersRequest getOrdersRequest) throws JsonProcessingException {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(getOrdersRequest);
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        return mapToResponse(res, GetOrdersResponse.class);
                    })
                    .orElseThrow(() -> new RuntimeException("Error baselinker api"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }
}
