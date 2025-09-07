package pl.thinkdata.droptop.baselinker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.AddProductResponse;
import pl.thinkdata.droptop.baselinker.model.BaselinkerExportLog;
import pl.thinkdata.droptop.baselinker.repository.BaselinkerExportLogRepository;
import pl.thinkdata.droptop.database.model.Product;

@Service
@RequiredArgsConstructor
public class BaselinkerLogService {

    private final BaselinkerExportLogRepository repository;

    public BaselinkerExportLog sendSuccesExport(Product product, AddProductResponse response) {
        BaselinkerExportLog log = BaselinkerExportLog.builder()
                .product(product)
                .baselinkerId(response.getProductId())
                .build();
        return repository.save(log);
    }
}
