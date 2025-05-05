package pl.thinkdata.droptop.dto.catalog;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CATALOG")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Catalog {

    @XmlElement(name = "CatalogResponse-Summary")
    private CatalogResponseSummary summary;

    @XmlElement(name = "rc")
    private Rc rc;
}

