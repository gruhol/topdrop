package pl.thinkdata.droptop.api.dto.catalog;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Warnings {

    @XmlElement(name = "d")
    private List<String> messages;
}
