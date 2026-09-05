package pl.thinkdata.droptop.database.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.thinkdata.droptop.database.dto.analytics.OrderProfitSummary;
import pl.thinkdata.droptop.database.service.AnalyticsService;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/analytics")
    public String getAnalytics(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<OrderProfitSummary> summary = analyticsService.getOrderProfitSummary(pageable);

        model.addAttribute("summary", summary);
        model.addAttribute("totals", analyticsService.getOrderProfitTotals());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", summary.getTotalPages());
        return "database/analytics";
    }
}
