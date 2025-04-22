package pl.thinkdata.droptop.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@RestController
public class ApiExternalController {

    public static final String EXTERNAL_OPERATION_INVOKE_RESULT = "ExternalOperationInvokeResult";
    public static final String RESULT = "Result";
    private final WebClient webClient;

    public ApiExternalController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://test.platon.com.pl").build();
    }

    @GetMapping(value = "/stocks", produces = MediaType.APPLICATION_XML_VALUE)
    public String getStock() {
        String soapXml = """
    <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
        <s:Body>
            <ExternalOperationInvoke xmlns="http://commersoft.pl/PostMan.Transfer">
                <OperationInfo>PEV4dGVybmFsT3BlcmF0aW9uSW5mbz48T3BlcmF0aW9uSWRlbnQ+Z2V0U3RvY2tzPC9PcGVyYXRpb25JZGVudD48Q29tcGFueUd1aWQ+MmQ3YTMwOTctZDlkMS00MTQwLWEwMjQtMmM4MDU0NzExZGQ3PC9Db21wYW55R3VpZD48VHJhbnNhY3Rpb25JZGVudD5mOTZhOWUyMC00OGU4LTQxMjYtYjFmMi1kYmZhMWFjMzVjYjQ8L1RyYW5zYWN0aW9uSWRlbnQ+PFVzZXJOYW1lPnBsYXRvbl90ZXN0PC9Vc2VyTmFtZT48UGFzc3dvcmQ+UGxhdG9uVGVzdDEhPC9QYXNzd29yZD48L0V4dGVybmFsT3BlcmF0aW9uSW5mbz4=</OperationInfo>
                <OperationParams>PEV4dGVybmFsT3BlcmF0aW9uUGFyYW1zPjxQYXJhbXM+UEVWNGNHOXlkRDQ4VUdGblpWTnBlbVUrTVRBd1BDOVFZV2RsVTJsNlpUNDhVR0ZuWlU1dlBqRThMMUJoWjJWT2J6NDhiR0Z6ZEVOb1lXNW5aV1JFWVhSbFBqSXdNak10TURFdE1EZFVNVEU2TkRBNk1ETXVOVE00T0RZMlBDOXNZWE4wUTJoaGJtZGxaRVJoZEdVK1BDOUZlSEJ2Y25RKzwvUGFyYW1zPjwvRXh0ZXJuYWxPcGVyYXRpb25QYXJhbXM+</OperationParams>
            </ExternalOperationInvoke>
        </s:Body>
    </s:Envelope>
    """;

        try {
            ResponseEntity<String> response = webClient.post()
                    .uri("/csConnector/CommS_WCF_TransferService.svc")
                    .header("Content-Type", "text/xml")
                    .header("SOAPAction", "http://commersoft.pl/PostMan.Transfer/ICommS_WCF_TransferService/ExternalOperationInvoke")
                    .bodyValue(soapXml)
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
                return "❌ Inny status: " + response.getStatusCode() + "\nOdpowiedź:\n" + response.getBody();
            } else {
                return "❌ Brak odpowiedzi z serwera.";
            }

        } catch (Exception e) {
            return "⚠️ Błąd połączenia: " + e.getMessage();
        }
    }

    private static String decodeBase64(String base64String) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd dekodowania Base64: " + e.getMessage());
            return null;
        }
    }

    private static String extractXMLByTag(String xml, String tag) {
        try {
            int start = xml.indexOf("<" + tag + ">") + ("<" + tag + ">").length();
            int end = xml.indexOf("</" + tag + ">");
            return xml.substring(start, end).trim();
        } catch (Exception e) {
            return null;
        }
    }
}
