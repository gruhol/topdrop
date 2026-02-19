package pl.thinkdata.droptop.api.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.Product;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiProductService {

    private final ProductRepository productRepository;

    public void updateAll(List<Product> updatedProducts) {
        for (Product product : updatedProducts) {
            updateProductByEan(product.getEan(), product);
        }
    }

    public void updateProductByEan(String ean, Product updatedProduct) {
        Product existingProduct = productRepository.findByEan(ean)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with EAN: " + ean));

        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setIsbn(updatedProduct.getIsbn());
        existingProduct.setReleaseDate(updatedProduct.getReleaseDate());
        existingProduct.setStatus(updatedProduct.getStatus());
        existingProduct.setImg(updatedProduct.getImg());
        existingProduct.setAuthor(updatedProduct.getAuthor());
        existingProduct.setSeries(updatedProduct.getSeries());
        existingProduct.setTranslator(updatedProduct.getTranslator());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPublisher(updatedProduct.getPublisher());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setReleaseYear(updatedProduct.getReleaseYear());
        existingProduct.setCoverType(updatedProduct.getCoverType());
        existingProduct.setPagesNumber(updatedProduct.getPagesNumber());
        existingProduct.setWidth(updatedProduct.getWidth());
        existingProduct.setHeight(updatedProduct.getHeight());
        existingProduct.setEdition(updatedProduct.getEdition());
        existingProduct.setWeight(updatedProduct.getWeight());
        existingProduct.setVat(updatedProduct.getVat());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setType(updatedProduct.getType());
        existingProduct.setDepth(updatedProduct.getDepth());
        existingProduct.setApprovalNumber(updatedProduct.getApprovalNumber());
        existingProduct.setPcn(updatedProduct.getPcn());
        existingProduct.setManufacturingCountryCode(updatedProduct.getManufacturingCountryCode());
        existingProduct.setDateOperator(updatedProduct.getDateOperator());
        existingProduct.setGpsrSekcja(updatedProduct.getGpsrSekcja());

        productRepository.save(existingProduct);
    }
}
