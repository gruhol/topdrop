package pl.thinkdata.droptop.database.dto.analytics;

import java.math.BigDecimal;

public interface OrderProfitTotals {
    BigDecimal getPrzychodBrutto();
    BigDecimal getKosztZakupuBrutto();
    BigDecimal getZyskBrutto();
    BigDecimal getZyskNetto();
    BigDecimal getPozycjeBezCenyZakupu();
}
