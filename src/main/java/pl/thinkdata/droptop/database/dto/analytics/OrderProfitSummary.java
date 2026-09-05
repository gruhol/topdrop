package pl.thinkdata.droptop.database.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderProfitSummary {
    Long getNumerZamowienia();
    LocalDateTime getDataZamowienia();
    BigDecimal getPrzychodBrutto();
    BigDecimal getKosztZakupuBrutto();
    BigDecimal getZyskBrutto();
    BigDecimal getZyskNetto();
    BigDecimal getPozycjeBezCenyZakupu();
}
