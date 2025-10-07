package pl.thinkdata.droptop.baselinker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.model.BaselinkerExportLog;
import pl.thinkdata.droptop.baselinker.repository.BaselinkerExportLogRepository;
import pl.thinkdata.droptop.database.model.Product;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BaselinkerLogService {

    private final BaselinkerExportLogRepository repository;

    public BaselinkerExportLog sendSuccesExport(Product product, AddProductResponse response) {
        return repository.findByProductId(product.getId())
                .map(log -> {
                    log.setUpdateDate(LocalDateTime.now());
                    return repository.save(log);
                }).orElseGet(() -> {
                    BaselinkerExportLog newLog = BaselinkerExportLog.builder()
                            .product(product)
                            .baselinkerId(response.getProductId())
                            .build();
                    return repository.save(newLog);
                });
    }
}
