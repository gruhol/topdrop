package pl.thinkdata.droptop.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.dto.GetStocksDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.stock.Stock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static pl.thinkdata.droptop.utils.Base64Coder.decodeBase64;
import static pl.thinkdata.droptop.utils.PlatonXMLGenerator.*;

@Service
public class GetStocksExternalService extends BaseExternalService implements ExternalServiceable<GetStocksDto> {

    private final String platonApiMethodGetStocks;

    public GetStocksExternalService(@Value("${platon.api.method.getstocks}") String platonApiMethodGetStocks) {
        this.platonApiMethodGetStocks = platonApiMethodGetStocks;
    }

    @Override
    public PlatonResponse get(GetStocksDto getStocksDto) {
        String orderId = getTransactionId(getStocksDto.getTransactionNumber());
        String operationInfo = prepareOperationInfo("getStocks", platonApiMethodGetStocks, platonUser, platonPassword, orderId); //00000000-0000-2025-0426-000000000001
        String parameters = prepareGetStockParameters(getStocksDto.getPageSize(),getStocksDto.getPageNo(), getStocksDto.getLastChangeDate());
        String request = prepareRequest(operationInfo, parameters);

        try {
            ResponseEntity<String> response = getDataFromWebClient(request);

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                String result = null;
                String innerBase64 = extractXMLByTag(responseBody, EXTERNAL_OPERATION_INVOKE_RESULT);

                JAXBContext context = JAXBContext.newInstance(Stock.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();

                if (innerBase64 != null) {
                    String secondLevelDecoded = decodeBase64(innerBase64);
                    String hashresult = extractXMLByTag(secondLevelDecoded, RESULT);
                    result = decodeBase64(hashresult);
                    StringReader reader = new StringReader(result);
                    return PlatonResponse.builder()
                            .stock((Stock) unmarshaller.unmarshal(reader))
                            .build();
                }
                StringReader reader = new StringReader(result);
                return PlatonResponse.builder()
                        .stock((Stock) unmarshaller.unmarshal(reader))
                        .build();

            } else if (response != null) {
                return createPlatonResponse("Status: " + response.getStatusCode() + "\nResponse:\n" + response.getBody());
            } else {
                return createPlatonResponse("Response not found");
            }

        } catch (Exception e) {
            return getPlatonExceptionResponse(e);
        }
    }
}
