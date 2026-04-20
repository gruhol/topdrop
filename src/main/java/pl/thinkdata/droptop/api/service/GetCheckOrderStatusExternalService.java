package pl.thinkdata.droptop.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.checkOrderStatus.CheckOrderStatusDto;
import pl.thinkdata.droptop.database.model.order.Order;
import pl.thinkdata.droptop.database.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

import static pl.thinkdata.droptop.common.utils.Base64Coder.decodeBase64;
import static pl.thinkdata.droptop.common.utils.PlatonXMLGenerator.*;

@Service
public class GetCheckOrderStatusExternalService extends BaseExternalService implements ExternalServiceable<CheckOrderStatusDto> {

    private final String platonApiMethodCheckOrderStatus;
    private final OrderRepository orderRepository;

    public GetCheckOrderStatusExternalService(
            @Value("${platon.api.method.checkorderstatus}") String platonApiMethodCheckOrderStatus,
            OrderRepository orderRepository) {
        this.platonApiMethodCheckOrderStatus = platonApiMethodCheckOrderStatus;
        this.orderRepository = orderRepository;
    }

    @Override
    public PlatonResponse get(CheckOrderStatusDto dto) {
        String transactionId = getTransactionId(dto.getTransactionNumber());
        String operationInfo = prepareOperationInfo("CHECKORDERSTATUS", platonApiMethodCheckOrderStatus, platonUser, platonPassword, transactionId);
        String parameters = prepareCheckOrderStatus(dto);
        String request = prepareRequest(operationInfo, parameters);

        try {
            ResponseEntity<String> response = getDataFromWebClient(request);

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                String innerBase64 = extractXMLByTag(responseBody, EXTERNAL_OPERATION_INVOKE_RESULT);

                if (innerBase64 != null) {
                    String secondLevelDecoded = decodeBase64(innerBase64);
                    String errorMessage = extractXMLByTag(secondLevelDecoded, "ErrorMessage");
                    if (errorMessage != null) {
                        return createPlatonResponse(errorMessage);
                    }
                    String resultBase64 = extractXMLByTag(secondLevelDecoded, RESULT);
                    if (resultBase64 != null) {
                        String orderResponseXml = decodeBase64(resultBase64);
                        String responseType = extractXMLByTag(orderResponseXml, "ResponseType");
                        if (responseType != null && responseType.startsWith("-")) {
                            String responseMessage = extractXMLByTag(orderResponseXml, "ResponseMessage");
                            return createPlatonErrorResponse(responseMessage != null ? responseMessage : "Błąd Platon (kod: " + responseType + ")");
                        }
                    }
                    saveOrderStatus(dto.getOrderNumber(), secondLevelDecoded);
                    return createPlatonResponse(secondLevelDecoded);
                }
                return createPlatonResponse(responseBody);

            } else if (response != null) {
                String errorMsg = "Status: " + response.getStatusCode() + "\nResponse:\n" + response.getBody();
                return createPlatonResponse(errorMsg);
            } else {
                return createPlatonResponse("Response not found");
            }

        } catch (Exception e) {
            return getPlatonExceptionResponse(e);
        }
    }

    private void saveOrderStatus(Long orderNumber, String responseXml) {
        String resultBase64 = extractXMLByTag(responseXml, RESULT);
        if (resultBase64 == null) {
            return;
        }
        String orderResponseXml = decodeBase64(resultBase64);
        String orderStatus = extractXMLByTag(orderResponseXml, "OrderStatus");
        if (orderStatus == null) {
            return;
        }
        List<String> packageNumbers = extractAllXMLByTag(orderResponseXml, "PackageNumber");
        String packageNumberValue = packageNumbers.isEmpty() ? null : String.join(",", packageNumbers);

        Optional<Order> orderOpt = orderRepository.findByOrderId(orderNumber);
        orderOpt.ifPresent(order -> {
            order.setPlatonOrderStatus(orderStatus);
            order.setPlatonPackageNumber(packageNumberValue);
            orderRepository.save(order);
        });
    }
}