package pl.thinkdata.droptop.api.dto.catalog;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class Gpsr {

    @XmlElement(name = "n")
    private String name;

    @XmlElement(name = "c")
    private String countryCode;

    @XmlElement(name = "s")
    private String street;

    @XmlElement(name = "h")
    private String house;

    @XmlElement(name = "l")
    private String apartment;

    @XmlElement(name = "pc")
    private String postalCode;

    @XmlElement(name = "p")
    private String city;

    @XmlElement(name = "e")
    private String email;
}
