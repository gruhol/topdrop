package pl.thinkdata.droptop.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;
import pl.thinkdata.droptop.api.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.common.service.ImageService;
import pl.thinkdata.droptop.database.model.Product;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ImageService imageService;

    public Product mapToProduct(ProductFromXml product, String url)  {
        return Product.builder()
                .ean(product.getEan())
                .isbn(product.getIsbn())
                .title(product.getTitle())
                .releaseDate(product.getReleaseDate())
                .status(product.getStatus())
                .img(imageService.uploadImage(url + product.getImg(), "product", product.getEan()))
                .author(product.getAuthor())
                .series(product.getSeries())
                .translator(product.getTranslator())
                .category(product.getCategory())
                .publisher(product.getPublisher())
                .description(product.getDescription())
                .releaseYear(product.getReleaseYear())
                .coverType(product.getCoverType())
                .pagesNumber(product.getPagesNumber())
                .width(product.getWidth())
                .height(product.getHeight())
                .edition(product.getEdition())
                .weight(product.getWeight())
                .vat(product.getVat())
                .price(product.getPrice())
                .type(product.getType())
                .depth(product.getDepth())
                .approvalNumber(product.getApprovalNumber())
                .pcn(product.getPcn())
                .manufacturingCountryCode(product.getManufacturingCountryCode())
                .build();
    }
}
