package pl.thinkdata.droptop.database.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.product.Product;
import pl.thinkdata.droptop.database.model.product.SyncStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProducts(Pageable pageable, String status, SyncStatus syncStatus) {
        return productRepository.findAllByStatusAndSyncStatus(status, syncStatus, pageable);
    }

    public List<String> getDistinctStatuses() {
        return productRepository.findDistinctStatuses();
    }

    public Page<Product> findProductsByKeyWords(String keyword, Pageable pageable) {
        Page<Product> resultByEan = productRepository.findAllByEan(keyword, pageable);
        if (!resultByEan.isEmpty()) {
            return resultByEan;
        }
        return productRepository.findAllByTitleContaining(keyword, pageable);
    }
}
