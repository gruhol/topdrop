package pl.thinkdata.droptop.utils;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static pl.thinkdata.droptop.utils.Base64Coder.encodeBase64;

public class XMLGenerator {

    public static String prepareRequest(String operationInfo, String parameters) {
        String soapXmlTemple =
                """
                <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/">
                    <s:Body>
                        <ExternalOperationInvoke xmlns="http://commersoft.pl/PostMan.Transfer">
                            <OperationInfo>%s</OperationInfo>
                            <OperationParams>%s</OperationParams>
                        </ExternalOperationInvoke>
                    </s:Body>
                </s:Envelope>
                """;

        return String.format(soapXmlTemple, operationInfo, parameters);
    }

    public static String prepareOperationInfo(String nameMethod, String companyGuid, String user, String password, String transactionId) {
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

    public static String prepareGetStockParameters(String pageSize, String pageNo, LocalDateTime lastChangedDate) {
        String parametersTemplate = """
                            <Export>
                            <PageSize>%s</PageSize>
                            <PageNo>%s</PageNo>
                            </Export>
                            """;

        String parametersTemplateWithLastChangedDate = """
                            <Export>
                            <PageSize>%s</PageSize>
                            <PageNo>%s</PageNo>
                            <lastChangedDate>%s</lastChangedDate>
                            </Export>
                            """;

        String parameters = isNull(lastChangedDate)
                ? String.format(parametersTemplate, pageSize, pageNo)
                : String.format(parametersTemplateWithLastChangedDate, pageSize, pageNo, lastChangedDate);

        String externalOperationParamsTemplate = """
                    <ExternalOperationParams>
                        <Params>%s</Params>
                    </ExternalOperationParams>
                    """;

        String externalOperationParams = String.format(externalOperationParamsTemplate, encodeBase64(parameters));
        return encodeBase64(externalOperationParams);
    }
}
