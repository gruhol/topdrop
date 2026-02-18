package pl.thinkdata.droptop.database.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.thinkdata.droptop.api.model.Category;
import pl.thinkdata.droptop.baselinker.model.BaselinkerExportLog;

import java.util.Comparator;
import java.util.List;

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
    private String stringCategory;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "contractorName",        column = @Column(name = "gpsr_contractor_name")),
            @AttributeOverride(name = "contractorCountryCode", column = @Column(name = "gpsr_contractor_country_code")),
            @AttributeOverride(name = "street",                column = @Column(name = "gpsr_street")),
            @AttributeOverride(name = "houseNumber",           column = @Column(name = "gpsr_house_number")),
            @AttributeOverride(name = "apartmentNumber",       column = @Column(name = "gpsr_apartment_number")),
            @AttributeOverride(name = "postalCode",            column = @Column(name = "gpsr_postal_code")),
            @AttributeOverride(name = "city",                  column = @Column(name = "gpsr_city")),
            @AttributeOverride(name = "email",                 column = @Column(name = "gpsr_email"))
    })
    private GpsrData gpsrSekcja;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductOfferLog> offers;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private BaselinkerExportLog exportLog;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status")
    private SyncStatus syncStatus;


    public ProductOfferLog getLatestOffer() {
        return this.getOffers().stream()
                .max(Comparator.comparing(ProductOfferLog::getFetchedAt))
                .orElse(null);
    }
}
