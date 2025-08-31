package pl.thinkdata.droptop.mapper;

import org.apache.commons.text.StringEscapeUtils;
import pl.thinkdata.droptop.api.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.database.model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class ProductMapper {

    public static Product mapToProduct(ProductFromXml product, String url) {
        return Product.builder()
                .ean(product.getEan())
                .isbn(product.getIsbn())
                .title(product.getTitle())
                .releaseDate(product.getReleaseDate())
                .status(product.getStatus())
                .img(getBase64Img(StringEscapeUtils.unescapeHtml4(url) + product.getImg()))
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

    private static String getBase64Img(String urlImages)  {
        try {
            URL url = new URL(urlImages);
            byte[] imageBytes;
            try (InputStream in = url.openStream()) {
                imageBytes = in.readAllBytes();
            }
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/jpeg;base64," + base64;
        } catch (IOException e) {
            return "";
        }
    }
}
