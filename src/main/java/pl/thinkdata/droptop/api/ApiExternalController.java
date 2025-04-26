package pl.thinkdata.droptop.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.Base64;

@RestController
public class ApiExternalController {

    public static final String EXTERNAL_OPERATION_INVOKE_RESULT = "ExternalOperationInvokeResult";
    public static final String RESULT = "Result";
    private final WebClient webClient;
    private String platonUser;
    private String platonPassword;
    private String platonApiMethodGetStocks;

    public ApiExternalController(WebClient.Builder webClientBuilder,
                                 @Value("${platon.api.user}") String platonUser,
                                 @Value("${platon.api.password}") String platonPassword,
                                 @Value("${platon.api.method.getstocks}") String platonApiMethodGetStocks) {
        this.webClient = webClientBuilder.baseUrl("https://test.platon.com.pl").build();
        this.platonUser = platonUser;
        this.platonPassword = platonPassword;
        this.platonApiMethodGetStocks = platonApiMethodGetStocks;
    }

    private String prepareOperationInfo(String nameMethod, String companyGuid, String user, String password, String transactionId) {
        String operationInfo =
            "<ExternalOperationInfo>" +
                "<OperationIdent>" + nameMethod + "</OperationIdent>" +
                "<CompanyGuid>" + companyGuid + "</CompanyGuid>" +
                "<TransactionIdent>" + transactionId + "</TransactionIdent>" +
                "<UserName>" + user + "</UserName>" +
                "<Password>" + password + "</Password>" +
            "</ExternalOperationInfo>";
        return encodeBase64(operationInfo);
    }

    @GetMapping(value = "/stocks", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getStock() {
        String operationInfo = prepareOperationInfo("getStocks", platonApiMethodGetStocks, platonUser, platonPassword, "00000000-0000-2025-0426-000000000001"); //00000000-0000-2025-0426-000000000001

        String soapXmlTemple =
                """
        <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
            <s:Body>
                <ExternalOperationInvoke xmlns="http://commersoft.pl/PostMan.Transfer">
                    <OperationInfo>%s</OperationInfo>
                    <OperationParams>PEV4dGVybmFsT3BlcmF0aW9uUGFyYW1zPjxQYXJhbXM+UEVWNGNHOXlkRDQ4VUdGblpWTnBlbVUrTVRBd1BDOVFZV2RsVTJsNlpUNDhVR0ZuWlU1dlBqRThMMUJoWjJWT2J6NDhiR0Z6ZEVOb1lXNW5aV1JFWVhSbFBqSXdNak10TURFdE1EZFVNVEU2TkRBNk1ETXVOVE00T0RZMlBDOXNZWE4wUTJoaGJtZGxaRVJoZEdVK1BDOUZlSEJ2Y25RKzwvUGFyYW1zPjwvRXh0ZXJuYWxPcGVyYXRpb25QYXJhbXM+</OperationParams>
                </ExternalOperationInvoke>
            </s:Body>
        </s:Envelope>
        """;

        String soapXml2 = String.format(soapXmlTemple, operationInfo);

        try {
            ResponseEntity<String> response = webClient.post()
                    .uri("/csConnector/CommS_WCF_TransferService.svc")
                    .header("Content-Type", "text/xml; charset=utf-8")
                    .header("SOAPAction", "http://commersoft.pl/PostMan.Transfer/ICommS_WCF_TransferService/ExternalOperationInvoke")
                    .bodyValue(soapXml2)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                String wynik = null;
                String innerBase64 = extractXMLByTag(responseBody, EXTERNAL_OPERATION_INVOKE_RESULT);

                if (innerBase64 != null) {
                    String secondLevelDecoded = decodeBase64(innerBase64);
                    String hashresult = extractXMLByTag(secondLevelDecoded, RESULT);
                    wynik = decodeBase64(hashresult);
                }

                return wynik;


            } else if (response != null) {
                return "Status: " + response.getStatusCode() + "\nResponse:\n" + response.getBody();
            } else {
                return "Response not found";
            }

        } catch (Exception e) {
            if (e instanceof WebClientResponseException webClientException) {
                String responseBody = webClientException.getResponseBodyAsString();
                String soapMessage = extractXMLByTag(responseBody, "Message");
                if (soapMessage != null) {
                    return "SOAP messange error: " + soapMessage;
                }
            }

            return "Error connection: " + e.getMessage();
        }
    }

    private static String decodeBase64(String toDecode) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(toDecode);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd dekodowania Base64: " + e.getMessage());
            return null;
        }
    }

    private static String encodeBase64(String toEncode) {
        try {
            return Base64.getEncoder().encodeToString(toEncode.getBytes());
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd enkodowania Base64: " + e.getMessage());
            return null;
        }
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
