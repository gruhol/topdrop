package pl.thinkdata.droptop.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.checkOrderStatus.CheckOrderStatusDto;
import pl.thinkdata.droptop.database.model.order.OrderSendLog;
import pl.thinkdata.droptop.database.repository.OrderSendLogRepository;

import java.time.LocalDateTime;

import static pl.thinkdata.droptop.common.utils.Base64Coder.decodeBase64;
import static pl.thinkdata.droptop.common.utils.PlatonXMLGenerator.*;

@Service
public class GetCheckOrderStatusExternalService extends BaseExternalService implements ExternalServiceable<CheckOrderStatusDto> {

    private final String platonApiMethodCheckOrderStatus;
    private final OrderSendLogRepository orderSendLogRepository;

    public GetCheckOrderStatusExternalService(
            @Value("${platon.api.method.checkorderstatus}") String platonApiMethodCheckOrderStatus,
            OrderSendLogRepository orderSendLogRepository) {
        this.platonApiMethodCheckOrderStatus = platonApiMethodCheckOrderStatus;
        this.orderSendLogRepository = orderSendLogRepository;
    }

    @Override
    public PlatonResponse get(CheckOrderStatusDto dto) {
        String transactionId = getTransactionId(dto.getTransactionNumber());
        String operationInfo = prepareOperationInfo("CHECKORDERSTATUS", platonApiMethodCheckOrderStatus, platonUser, platonPassword, transactionId);
        String parameters = prepareCheckOrderStatus(dto);
        String request = prepareRequest(operationInfo, parameters);

        OrderSendLog orderSendLog = orderSendLogRepository.save(OrderSendLog.builder()
                .orderNumber(dto.getOrderNumber())
                .request(request)
                .requestDate(LocalDateTime.now())
                .status("PENDING")
                .build());
        try {
            ResponseEntity<String> response = getDataFromWebClient(request);

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                String innerBase64 = extractXMLByTag(responseBody, EXTERNAL_OPERATION_INVOKE_RESULT);

                if (innerBase64 != null) {
                    String secondLevelDecoded = decodeBase64(innerBase64);
                    String errorMessage = extractXMLByTag(secondLevelDecoded, "ErrorMessage");
                    if (errorMessage != null) {
                        orderSendLog.setStatus("ERROR");
                        orderSendLog.setErrorMessage(errorMessage);
                        orderSendLog.setResponse(secondLevelDecoded);
                        orderSendLogRepository.save(orderSendLog);
                        return createPlatonResponse(errorMessage);
                    }
                    orderSendLog.setStatus("SUCCESS");
                    orderSendLog.setResponse(secondLevelDecoded);
                    orderSendLogRepository.save(orderSendLog);
                    return createPlatonResponse(secondLevelDecoded);
                }

                orderSendLog.setStatus("SUCCESS");
                orderSendLog.setResponse(responseBody);
                orderSendLogRepository.save(orderSendLog);
                return createPlatonResponse(responseBody);

            } else if (response != null) {
                String errorMsg = "Status: " + response.getStatusCode() + "\nResponse:\n" + response.getBody();
                orderSendLog.setStatus("ERROR");
                orderSendLog.setErrorMessage(errorMsg);
                orderSendLogRepository.save(orderSendLog);
                return createPlatonResponse(errorMsg);
            } else {
                orderSendLog.setStatus("ERROR");
                orderSendLog.setErrorMessage("Response not found");
                orderSendLogRepository.save(orderSendLog);
                return createPlatonResponse("Response not found");
            }

        } catch (Exception e) {
            orderSendLog.setStatus("ERROR");
            orderSendLog.setErrorMessage(e.getMessage());
            orderSendLogRepository.save(orderSendLog);
            return getPlatonExceptionResponse(e);
        }
    }
}