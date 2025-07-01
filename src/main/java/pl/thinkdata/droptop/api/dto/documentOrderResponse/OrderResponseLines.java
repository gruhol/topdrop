package pl.thinkdata.droptop.api.dto.documentOrderResponse;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class OrderResponseLines {

    @XmlElement(name = "Line")
    private List<Line> lines;
}

