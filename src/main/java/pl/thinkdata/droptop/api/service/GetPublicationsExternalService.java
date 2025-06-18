package pl.thinkdata.droptop.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.dto.GetPublicationsDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.catalog.Catalog;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static pl.thinkdata.droptop.utils.Base64Coder.decodeBase64;
import static pl.thinkdata.droptop.utils.PlatonXMLGenerator.*;

@Service
public class GetPublicationsExternalService extends BaseExternalService implements ExternalServiceable<GetPublicationsDto>{

    private final String platonApiMethodGetPublications;

    public GetPublicationsExternalService(@Value("${platon.api.method.getpublications}") String platonApiMethodGetPublications) {
        this.platonApiMethodGetPublications = platonApiMethodGetPublications;
    }

    @Override
    public PlatonResponse get(GetPublicationsDto dto) {
        String transactionId = getTransactionId(dto.getTransactionNumber());
        String operationInfo = prepareOperationInfo("getPublications", platonApiMethodGetPublications, platonUser, platonPassword, transactionId);
        String parameters = prepareGetStockParameters(dto.getPageSize(),dto.getPageNo(), dto.getLastChangeDate());
        String request = prepareRequest(operationInfo, parameters);

        try {
            ResponseEntity<String> response = getDataFromWebClient(request);

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                String result = null;
                String innerBase64 = extractXMLByTag(responseBody, EXTERNAL_OPERATION_INVOKE_RESULT);

                JAXBContext context = JAXBContext.newInstance(Catalog.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();

                if (innerBase64 != null) {
                    String secondLevelDecoded = decodeBase64(innerBase64);
                    String hashresult = extractXMLByTag(secondLevelDecoded, RESULT);
                    result = decodeBase64(hashresult);
                    StringReader reader = new StringReader(result);
                    return PlatonResponse.builder()
                            .catalog((Catalog) unmarshaller.unmarshal(reader))
                            .build();
                }
                StringReader reader = new StringReader(result);
                return PlatonResponse.builder()
                        .catalog((Catalog) unmarshaller.unmarshal(reader))
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
