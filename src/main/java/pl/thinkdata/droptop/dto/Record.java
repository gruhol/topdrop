package pl.thinkdata.droptop.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Record {

    @XmlElement(name = "a")
    private String ean;

    @XmlElement(name = "b")
    private String productCode;

    @XmlElement(name = "c")
    private String quantity;

    @XmlElement(name = "d")
    private double netPrice;

    @XmlElement(name = "e")
    private double grossPrice;

    @XmlElement(name = "f")
    private int discount;

    @XmlElement(name = "g")
    private String quantity2;
}
