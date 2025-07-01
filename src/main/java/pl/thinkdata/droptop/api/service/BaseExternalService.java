package pl.thinkdata.droptop.api.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import pl.thinkdata.droptop.api.dto.PlatonResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

public class BaseExternalService {
    public static final String EXTERNAL_OPERATION_INVOKE_RESULT = "ExternalOperationInvokeResult";
    public static final String RESULT = "Result";

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${platon.api.prod}")
    private boolean platonProd;

    @Value("${platon.api.user}")
    protected String platonUser;

    @Value("${platon.api.password}")
    protected String platonPassword;

    protected WebClient webClient;

    @PostConstruct
    private void initWebClient() {
        String url = platonProd ? "https://platon.com.pl" : "https://test.platon.com.pl";
        this.webClient = webClientBuilder.baseUrl(url)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    ResponseEntity<String> getDataFromWebClient(String request) {
        return webClient.post()
                .uri("/csConnector/CommS_WCF_TransferService.svc")
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "http://commersoft.pl/PostMan.Transfer/ICommS_WCF_TransferService/ExternalOperationInvoke")
                .bodyValue(request)
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    static PlatonResponse createPlatonResponse(String message) {
        return PlatonResponse.builder()
                .message(message)
                .build();
    }

    static PlatonResponse getPlatonExceptionResponse(Exception e) {
        if (e instanceof WebClientResponseException webClientException) {
            String responseBody = webClientException.getResponseBodyAsString();
            String soapMessage = extractXMLByTag(responseBody, "Message");
            if (soapMessage != null) {
                return createPlatonResponse("SOAP messange error: " + soapMessage);
            }
        }
        return createPlatonResponse("Error connection: " + e.getMessage());
    }

    static String getTransactionId(int orderNumber) {
        LocalDateTime reqTime = LocalDateTime.now();
        String fixedPrefix = "00000000-0000";
        String year = String.valueOf(reqTime.getYear());
        String monthDay = String.format("%02d%02d", reqTime.getMonthValue(), reqTime.getDayOfMonth());
        String formattedOrderNumber = String.format("%012d", orderNumber);
        return String.join("-", fixedPrefix, year, monthDay, formattedOrderNumber);
    }

    static String extractXMLByTag(String xml, String tagName) {
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
            System.err.println("Błąd parsowania XML: " + e.getMessage() + e.getLocalizedMessage());
        }
        return null;
    }
}
