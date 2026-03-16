package pl.thinkdata.droptop.database.model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceInfo {

    @Column(name = "invoice_fullname", length = 150)
    private String fullname;

    @Column(name = "invoice_company", length = 150)
    private String company;

    @Column(name = "invoice_nip", length = 30)
    private String nip;

    @Column(name = "invoice_address", length = 200)
    private String address;

    @Column(name = "invoice_city", length = 100)
    private String city;

    @Column(name = "invoice_state", length = 100)
    private String state;

    @Column(name = "invoice_postcode", length = 20)
    private String postcode;

    @Column(name = "invoice_country_code", length = 10)
    private String countryCode;

    @Column(name = "invoice_country", length = 100)
    private String country;

    @Column(name = "want_invoice")
    private boolean wantInvoice;
}
