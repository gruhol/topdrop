package pl.thinkdata.droptop.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String ean;
    private String isbn;
    private String title;
    @Column(name = "release_date")
    private String releaseDate;
    private String status;
    private String img;
    private String author;
    private String series;
    private String translator;
    private String category;
    private String publisher;
    @Lob
    private String description;
    @Column(name = "release_year")
    private String releaseYear;
    @Column(name = "cover_type")
    private String coverType;
    @Column(name = "pages_number")
    private int pagesNumber;
    private int width;
    private int height;
    private int edition;
    private int weight;
    private String vat;
    private double price;
    private String type;
    private double depth;
    @Column(name = "approval_number")
    private String approvalNumber;
    private String pcn;
    @Column(name = "manufacturing_country_code")
    private String manufacturingCountryCode;
    @Embedded
    private DateOperator dateOperator;
}
