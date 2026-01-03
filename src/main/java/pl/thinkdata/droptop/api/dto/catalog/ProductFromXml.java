package pl.thinkdata.droptop.api.dto.catalog;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductFromXml {

    @XmlElement(name = "a")
    private String id;

    @XmlElement(name = "b")
    private String ean;

    @XmlElement(name = "c")
    private String isbn;

    @XmlElement(name = "d")
    private String title;

    @XmlElement(name = "e")
    private String releaseDate;

    @XmlElement(name = "f")
    private String status;

    @XmlElement(name = "g")
    private String img;

    @XmlElement(name = "h")
    private String author;

    @XmlElement(name = "i")
    private String series;

    @XmlElement(name = "j")
    private String translator;

    @XmlElement(name = "k")
    private String category;

    @XmlElement(name = "l")
    private String publisher;

    @XmlElement(name = "m")
    private String description;

    @XmlElement(name = "n")
    private String releaseYear;

    @XmlElement(name = "o")
    private String coverType;

    @XmlElement(name = "p")
    private int pagesNumber;

    @XmlElement(name = "r")
    private int width;

    @XmlElement(name = "s")
    private int height;

    @XmlElement(name = "t")
    private int edition;

    @XmlElement(name = "u")
    private int weight;

    @XmlElement(name = "w")
    private String vat;

    @XmlElement(name = "x")
    private double price;

    @XmlElement(name = "y")
    private String type;

    @XmlElement(name = "z")
    private double depth;

    @XmlElement(name = "nd")
    private String approvalNumber;

    @XmlElement(name = "cn")
    private String pcn;

    @XmlElement(name = "cf")
    private String manufacturingCountryCode;

    @XmlElement(name = "wi")
    private Warnings warnings;

    @XmlElement(name = "sc")
    private Sc sc;

    @XmlElement(name = "fr")
    private Fr fr;

    @XmlElement(name = "gpsr")
    private Gpsr gpsr;
}