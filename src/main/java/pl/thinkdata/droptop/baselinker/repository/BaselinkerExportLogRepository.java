package pl.thinkdata.droptop.baselinker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.baselinker.model.BaselinkerExportLog;

import java.util.Optional;

public interface BaselinkerExportLogRepository extends JpaRepository<BaselinkerExportLog, Long> {

    Optional<BaselinkerExportLog> findByProductId(Long productId);
}
