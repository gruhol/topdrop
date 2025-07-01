package pl.thinkdata.droptop.api.dto.documentOrderResponse;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class OrderResponseHeader {

    @XmlElement(name = "OrderResponseNumber")
    private String orderResponseNumber;

    @XmlElement(name = "OrderResponseDate")
    private String orderResponseDate;

    @XmlElement(name = "ResponseType")
    private int responseType;

    @XmlElement(name = "ResponseMessage")
    private String responseMessage;

    @XmlElement(name = "OrderNumber")
    private String orderNumber;

    @XmlElement(name = "OrderDate")
    private String orderDate;
}
