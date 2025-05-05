package pl.thinkdata.droptop.dto.stock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "STOCK")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Stock {

    @XmlElement(name = "StocksResponse-Summary")
    private Summary summary;

    @XmlElement(name = "StocksResponse-Header")
    private Header header;

    @XmlElement(name = "r")
    private List<Record> records;
}

