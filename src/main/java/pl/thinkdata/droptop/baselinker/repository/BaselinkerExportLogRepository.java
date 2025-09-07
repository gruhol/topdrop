package pl.thinkdata.droptop.baselinker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.baselinker.model.BaselinkerExportLog;

public interface BaselinkerExportLogRepository extends JpaRepository<BaselinkerExportLog, Long> {
}
