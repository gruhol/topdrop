package pl.thinkdata.droptop.baselinker.component;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.thinkdata.droptop.config.service.SystemSettingService;

@Component
@RequiredArgsConstructor
public class BaselinkerExportScheduled {

    private final SystemSettingService systemSettingService;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void exportProducts() {
        boolean enabled = systemSettingService.getValue("baselinker_auto_export", Boolean.class);
        if (enabled) {
            System.out.println("Task enabled: " + System.currentTimeMillis());
        } else {
            System.out.println("Task stopped: " + System.currentTimeMillis());
        }
    }
}
