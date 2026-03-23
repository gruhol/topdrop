package pl.thinkdata.droptop.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.documentOrderResponse.DocumentOrderResponse;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderDropDto;
import pl.thinkdata.droptop.database.model.order.OrderSendLog;
import pl.thinkdata.droptop.database.repository.OrderSendLogRepository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.LocalDateTime;

import static pl.thinkdata.droptop.common.utils.Base64Coder.decodeBase64;
import static pl.thinkdata.droptop.common.utils.PlatonXMLGenerator.*;

@Service
public class GetOrderDropExternalService extends BaseExternalService implements ExternalServiceable<OrderDropDto> {

    private final String platonApiMethodOrderDrop;
    private final OrderSendLogRepository orderSendLogRepository;

    public GetOrderDropExternalService(@Value("${platon.api.method.order.drop}") String platonApiMethodOrderDrop,
                                       OrderSendLogRepository orderSendLogRepository) {
        this.platonApiMethodOrderDrop = platonApiMethodOrderDrop;
        this.orderSendLogRepository = orderSendLogRepository;
    }

    @Override
    public PlatonResponse get(OrderDropDto dto) {
        String transactionId = getTransactionId(dto.getTransactionNumber());
        String operationInfo = prepareOperationInfo("ORDERDROP", platonApiMethodOrderDrop, platonUser, platonPassword, transactionId);
        String parameters = prepareDocumentOrder(dto);
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
                String result = null;
                String innerBase64 = extractXMLByTag(responseBody, EXTERNAL_OPERATION_INVOKE_RESULT);

                JAXBContext context = JAXBContext.newInstance(DocumentOrderResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();

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
                    String hashresult = extractXMLByTag(secondLevelDecoded, RESULT);
                    result = decodeBase64(hashresult);
                    if (result == null) {
                        orderSendLog.setStatus("ERROR");
                        orderSendLog.setErrorMessage(secondLevelDecoded);
                        orderSendLogRepository.save(orderSendLog);
                        return createPlatonResponse(secondLevelDecoded);
                    }
                    StringReader reader = new StringReader(result);
                    orderSendLog.setStatus("SUCCESS");
                    orderSendLog.setResponse(result);
                    orderSendLogRepository.save(orderSendLog);
                    return PlatonResponse.builder()
                            .documentOrderResponse((DocumentOrderResponse) unmarshaller.unmarshal(reader))
                            .build();
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
