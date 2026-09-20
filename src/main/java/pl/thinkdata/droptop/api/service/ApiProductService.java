package pl.thinkdata.droptop.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.database.model.product.Product;

@Service
@RequiredArgsConstructor
public class ApiProductService {

    public void applyFields(Product existingProduct, Product updatedProduct) {
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
    }
}
