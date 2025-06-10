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
}
