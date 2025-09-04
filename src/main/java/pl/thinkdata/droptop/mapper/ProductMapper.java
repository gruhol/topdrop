package pl.thinkdata.droptop.mapper;

import org.apache.commons.text.StringEscapeUtils;
import pl.thinkdata.droptop.api.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.database.model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
            String extension = getExtension(url);

            byte[] imageBytes;
            try (InputStream in = url.openStream()) {
                imageBytes = in.readAllBytes();
            }
            return "data:" + extension + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            return "";
        }
    }

    private static String getExtension(URL url)  {
        try{
            HttpURLConnection connHttp = (HttpURLConnection) url.openConnection();
            connHttp.setRequestMethod("GET");

            String mimeType = connHttp.getContentType(); // np. "image/jpeg"
            if (mimeType == null) {
                throw new RuntimeException("Nie udało się odczytać typu MIME!");
            }
            return getExtensionFromMime(mimeType);
        } catch (IOException e) {
            return getExtensionFromMime("");
        }
    }

    private static String getExtensionFromMime(String mimeType) {
        switch (mimeType) {
            case "jpg":
            case "image/jpeg": return "jpeg";
            case "image/png": return "png";
            case "image/gif": return "gif";
            case "image/webp": return "webp";
            case "image/bmp": return "bmp";
            case "image/svg+xml": return "svg";
            default: return "bin"; // fallback
        }
    }
}
