package pl.thinkdata.droptop.api.dto.documentOrderResponse;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Line {

    @XmlElement(name = "Line-Item")
    private LineItem lineItem;
}
