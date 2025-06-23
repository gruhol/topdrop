package pl.thinkdata.droptop.api.dto.documentOrderResponse;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class LineItem {

    @XmlElement(name = "LineNumber")
    private int lineNumber;

    @XmlElement(name = "LineItemStatus")
    private int lineItemStatus;

    @XmlElement(name = "SupplierItemCode")
    private String supplierItemCode;

    @XmlElement(name = "OrderedQuantity")
    private double orderedQuantity;

    @XmlElement(name = "QuantityToBeDelivered")
    private double quantityToBeDelivered;

    @XmlElement(name = "UnitOfMeasure")
    private String unitOfMeasure;
}

