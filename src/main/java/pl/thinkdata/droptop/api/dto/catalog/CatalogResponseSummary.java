package pl.thinkdata.droptop.api.dto.catalog;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogResponseSummary {

    @XmlElement(name = "PageSize")
    private int pageSize;

    @XmlElement(name = "PageNo")
    private int pageNo;

    @XmlElement(name = "Total")
    private int total;

    @XmlElement(name = "lastChangedDate")
    private String lastChangedDate;
}
