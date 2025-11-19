package pl.thinkdata.droptop.common.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;
import pl.thinkdata.droptop.api.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.api.utils.CategoryGeneratorUtils;
import pl.thinkdata.droptop.common.service.ImageService;
import pl.thinkdata.droptop.database.model.Product;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public static final String PRODUCT = "product";

    private final ImageService imageService;
    private final CategoryGeneratorUtils categoryGeneratorUtils;

    public Product mapToProduct(ProductFromXml product, String url)  {

        return Product.builder()
                .ean(product.getEan())
                .isbn(product.getIsbn())
                .title(product.getTitle())
                .releaseDate(product.getReleaseDate())
                .status(product.getStatus())
                //.img(imageService.uploadImage(url + product.getImg(), PRODUCT, product.getEan())) // get data from server
                .img(StringEscapeUtils.unescapeHtml4(url + product.getImg())) //get data from product source
                .author(product.getAuthor())
                .series(product.getSeries())
                .translator(product.getTranslator())
                .stringCategory(product.getCategory())
                .category(categoryGeneratorUtils.parseStringToCategory(product, product.getType()))
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
