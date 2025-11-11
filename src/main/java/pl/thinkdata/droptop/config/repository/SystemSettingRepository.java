package pl.thinkdata.droptop.config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.config.model.SystemSetting;

import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    Optional<SystemSetting> findByKey(String key);
}
