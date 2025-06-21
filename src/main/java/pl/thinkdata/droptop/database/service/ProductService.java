package pl.thinkdata.droptop.database.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.Product;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findProductsByKeyWords(String keyword, Pageable pageable) {
        Page<Product> resultByEan = productRepository.findAllByEan(keyword, pageable);
        if (!resultByEan.isEmpty()) {
            return resultByEan;
        }
        return productRepository.findAllByTitleContaining(keyword, pageable);
    }
}
