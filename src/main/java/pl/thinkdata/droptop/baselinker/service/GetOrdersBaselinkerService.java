package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.order.GetOrdersRequest;
import pl.thinkdata.droptop.baselinker.dto.order.OrderBaselinker;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrdersBaselinkerService extends BaselinkerWebClientService implements BaselinkerSendable<List<OrderBaselinker>, GetOrdersRequest> {


    @Override
    public List<OrderBaselinker> sendRequest(GetOrdersRequest getOrdersRequest) throws JsonProcessingException {
        return List.of();
    }

    @Override
    public List<OrderBaselinker> mapToResponse(ResponseEntity<String> response, Class<List<OrderBaselinker>> clazz) {
        return BaselinkerSendable.super.mapToResponse(response, clazz);
    }
}
