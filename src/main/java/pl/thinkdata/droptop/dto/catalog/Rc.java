package pl.thinkdata.droptop.dto.catalog;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Rc {

    @XmlElement(name = "DataOdpowiedzi")
    private String responseDate;

    @XmlElement(name = "URLOkladkaLink")
    private String urlCoverBookLink;
}
