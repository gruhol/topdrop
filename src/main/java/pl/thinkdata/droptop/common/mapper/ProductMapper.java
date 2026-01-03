package pl.thinkdata.droptop.common.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;
import pl.thinkdata.droptop.api.dto.catalog.Fr;
import pl.thinkdata.droptop.api.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.api.dto.catalog.Sc;
import pl.thinkdata.droptop.api.utils.CategoryGeneratorUtils;
import pl.thinkdata.droptop.common.service.ImageService;
import pl.thinkdata.droptop.database.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .aditionalImgs(getaditionalImgs(product.getSc(), url))
                .fragments(getFragments(product.getFr(), url))
                .build();
    }

    private String getaditionalImgs(Sc images, String url) {
        return Optional.ofNullable(images)
                .map(Sc::getIds)
                .orElse(List.of())
                .stream()
                .map(img -> StringEscapeUtils.unescapeHtml4(url + img + ";"))
                .collect(Collectors.joining());
    }

    private String getFragments(Fr fragments, String url) {
        return Optional.ofNullable(fragments)
                .map(Fr::getIds)
                .orElse(List.of())
                .stream()
                .map(img -> StringEscapeUtils.unescapeHtml4(url + img + ";"))
                .collect(Collectors.joining());
    }
}
