package pl.thinkdata.droptop.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import pl.thinkdata.droptop.dto.GetPublicationsDto;
import pl.thinkdata.droptop.dto.GetStocksDto;
import pl.thinkdata.droptop.dto.PlatonResponse;
import pl.thinkdata.droptop.dto.catalog.Catalog;
import pl.thinkdata.droptop.dto.stock.Stock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.time.LocalDateTime;

import static pl.thinkdata.droptop.utils.Base64Coder.decodeBase64;
import static pl.thinkdata.droptop.utils.XMLGenerator.prepareGetStockParameters;
import static pl.thinkdata.droptop.utils.XMLGenerator.prepareOperationInfo;
import static pl.thinkdata.droptop.utils.XMLGenerator.prepareRequest;

@Service
public class ApiExternalService {

    public static final String EXTERNAL_OPERATION_INVOKE_RESULT = "ExternalOperationInvokeResult";
    public static final String RESULT = "Result";
    private final WebClient webClient;
    private final boolean platonProd;
    private final String platonUser;
    private final String platonPassword;
    private final String platonApiMethodGetStocks;
    private final String platonApiMethodGetPublications;

    public ApiExternalService(WebClient.Builder webClientBuilder,
                              @Value("${platon.api.prod}") boolean platonProd,
                              @Value("${platon.api.user}") String platonUser,
                              @Value("${platon.api.password}") String platonPassword,
                              @Value("${platon.api.method.getstocks}") String platonApiMethodGetStocks,
                              @Value("${platon.api.method.getpublications}") String platonApiMethodGetPublications) {
        this.platonProd = platonProd;
        String url = this.platonProd ? "https://platon.com.pl" : "https://test.platon.com.pl";
        this.webClient = webClientBuilder.baseUrl(url).build();
        this.platonUser = platonUser;
        this.platonPassword = platonPassword;
        this.platonApiMethodGetStocks = platonApiMethodGetStocks;
        this.platonApiMethodGetPublications = platonApiMethodGetPublications;
    }

    public PlatonResponse createPlatonResponse(GetStocksDto getStocksDto) {
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

    public PlatonResponse getPublications(GetPublicationsDto dto) {
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

    private ResponseEntity<String> getDataFromWebClient(String request) {
        return webClient.post()
                .uri("/csConnector/CommS_WCF_TransferService.svc")
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "http://commersoft.pl/PostMan.Transfer/ICommS_WCF_TransferService/ExternalOperationInvoke")
                .bodyValue(request)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    private PlatonResponse createPlatonResponse(String message) {
        return PlatonResponse.builder()
                .message(message)
                .build();
    }

    private PlatonResponse getPlatonExceptionResponse(Exception e) {
        if (e instanceof WebClientResponseException webClientException) {
            String responseBody = webClientException.getResponseBodyAsString();
            String soapMessage = extractXMLByTag(responseBody, "Message");
            if (soapMessage != null) {
                return createPlatonResponse("SOAP messange error: " + soapMessage);
            }
        }
        return createPlatonResponse("Error connection: " + e.getMessage());
    }

    private static String getTransactionId(int orderNumber) {
        LocalDateTime reqTime = LocalDateTime.now();
        String fixedPrefix = "00000000-0000";
        String year = String.valueOf(reqTime.getYear());
        String monthDay = String.format("%02d%02d", reqTime.getMonthValue(), reqTime.getDayOfMonth());
        String formattedOrderNumber = String.format("%012d", orderNumber);
        return String.join("-", fixedPrefix, year, monthDay, formattedOrderNumber);
    }

    private static String extractXMLByTag(String xml, String tagName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()));

            NodeList nodes = document.getElementsByTagName(tagName);
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent().trim();
            }
        } catch (Exception e) {
            System.err.println("Błąd parsowania XML: " + e.getMessage());
        }
        return null;
    }
}
