package pl.thinkdata.droptop.api.dto.catalog;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Rc {

    @XmlElement(name = "DataOdpowiedzi")
    private String responseDate;

    @XmlElement(name = "URLOkladkaLink")
    private String urlCoverBookLink;

    @XmlElementWrapper(name = "PRODUCTS")
    @XmlElement(name = "rp")
    private List<ProductFromXml> products;
}
