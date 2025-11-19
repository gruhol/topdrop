package pl.thinkdata.droptop.common.utils;

import pl.thinkdata.droptop.api.dto.orderDrop.DeliveryPoint;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderDropDto;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderLine;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static pl.thinkdata.droptop.common.utils.Base64Coder.encodeBase64;

public class PlatonXMLGenerator {

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

    public static String prepareExportParameters(int pageSize, int pageNo, LocalDateTime lastChangedDate) {
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

    public static String prepareDocumentOrder(OrderDropDto dto) {
        String orderHeader = prepareOrderHeader(dto);
        String deliveryPoint = prepareDeliveryPoint(dto.getDeliveryPoint());
        String lineItems = prepareOrderLine(dto.getOrderLine());

        String documentOrderTemplate = """
                <Document-Order>
                     <Order-Header>%s</Order-Header>
                     <Order-Parties>
                         <Buyer>
                             <AccountNumber>%s</AccountNumber>
                         </Buyer>
                         <DeliveryPoint>%s</DeliveryPoint>
                     </Order-Parties>
                     <Order-Lines>
                         <Line>%s</Line>
                     </Order-Lines>
                     <Order-Summary>
                         <TotalLines>2</TotalLines>
                         <TotalNetAmount>2900.00</TotalNetAmount>
                         <csTotalGrossAmount>3567.00</csTotalGrossAmount>
                     </Order-Summary>
                 </Document-Order>
                """;
        String documentOrder =  String.format(documentOrderTemplate, orderHeader, dto.getAccountNumber(), deliveryPoint, lineItems);
        String externalOperationParamsTemplate = """
                    <ExternalOperationParams>
                        <Params>%s</Params>
                    </ExternalOperationParams>
                    """;

        String externalOperationParams = String.format(externalOperationParamsTemplate, encodeBase64(documentOrder));
        return encodeBase64(externalOperationParams);
    }


    private static String prepareOrderLine(List<OrderLine> orderLines) {
        StringBuilder result = new StringBuilder();
        String lineItemTemplate = """
                <Line-Item>
                <LineNumber>%d</LineNumber>
                <SupplierItemCode>%s</SupplierItemCode>
                <OrderedQuantity>%s</OrderedQuantity>
                <UnitOfMeasure>szt</UnitOfMeasure>
                </Line-Item>
                """;

        for (int i = 0; i < orderLines.size(); i++) {
            OrderLine order = orderLines.get(i);
            result.append(String.format(
                    lineItemTemplate,
                    i + 1,
                    order.getSupplierItemCode(),
                    order.getOrderedQuantity()
            ));
        }
        return result.toString();
    }


    private static String prepareDeliveryPoint(DeliveryPoint deliveryPoint) {
        String deliveryPointTemplate = """
               <CustomerKind>%s</CustomerKind>
               <Name>%s</Name>
               <Surname>%s</Surname>
               <Street>%s</Street>
               <HNo>%s</HNo>
               %s
               <CityName>%s</CityName>
               <PostalCode>%s</PostalCode>
               <Post>%s</Post>
               <Email>%s</Email>
               <Phone>%s</Phone>
               <Country>%s</Country>
               <DeliveryMethod>%s</DeliveryMethod>
               <MachineName>%s</MachineName>
               """;
        return String.format(deliveryPointTemplate,
                deliveryPoint.getCustomerKind(),
                deliveryPoint.getName(),
                deliveryPoint.getSurname(),
                deliveryPoint.getStreet(),
                deliveryPoint.getHomeNumber(),
                isNull(deliveryPoint.getFlatNumber()) ? "" : "<LNo>" + deliveryPoint.getFlatNumber() + "</LNo>",
                deliveryPoint.getCityName(),
                deliveryPoint.getPostCode(),
                deliveryPoint.getPost(),
                deliveryPoint.getEmail(),
                deliveryPoint.getPhone(),
                deliveryPoint.getCountry(),
                deliveryPoint.getDeliveryMethod(),
                deliveryPoint.getMachineName());
    }

    private static String prepareOrderHeader(OrderDropDto dto) {
        String orderHeaderTemplate = """
                <OrderNumber>%s</OrderNumber>
                 <OrderDate>%s</OrderDate>
                 <Remarks>%s</Remarks>
                 <OrderCurrency>PLN</OrderCurrency>
                """;
        return String.format(orderHeaderTemplate, dto.getOrderNumber(), dto.getOrderDate(), dto.getOrderRemarks());
    }
}
