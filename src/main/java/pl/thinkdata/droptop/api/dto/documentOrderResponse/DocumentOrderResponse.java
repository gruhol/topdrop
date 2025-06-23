package pl.thinkdata.droptop.api.dto.documentOrderResponse;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Document-OrderResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class DocumentOrderResponse {

    @XmlElement(name = "OrderResponse-Header")
    private OrderResponseHeader orderResponseHeader;

    @XmlElement(name = "OrderResponse-Lines")
    private OrderResponseLines orderResponseLines;

}
