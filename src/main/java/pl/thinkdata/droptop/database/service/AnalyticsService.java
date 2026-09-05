package pl.thinkdata.droptop.database.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.database.dto.analytics.OrderProfitSummary;
import pl.thinkdata.droptop.database.dto.analytics.OrderProfitTotals;
import pl.thinkdata.droptop.database.repository.AnalyticsRepository;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public Page<OrderProfitSummary> getOrderProfitSummary(Pageable pageable) {
        return analyticsRepository.findOrderProfitSummary(pageable);
    }

    public OrderProfitTotals getOrderProfitTotals() {
        return analyticsRepository.getOrderProfitTotals();
    }
}
