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

    private static String getBase64Img(String urlImages) {
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

    private static String getExtension(URL url) {
        try {
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
            case "image/jpeg":
                return "jpeg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            case "image/bmp":
                return "bmp";
            case "image/svg+xml":
                return "svg";
            default:
                return "bin"; // fallback
        }
    }
}

//    private static String getBase64Img(String urlImages) {
//        try {
//            DownloadResult dl = download(urlImages);
//            // 2) Ustal MIME: najpierw z nagłówka, w razie czego „wąchamy” bajty
//            String mime = dl.contentType != null && dl.contentType.startsWith("image/")
//                    ? dl.contentType
//                    : sniffMime(dl.bytes);
//
//            if (mime == null) {
//                return "";
//            }
//
//            // 3) Base64 bez łamań linii (UWAGA: nie używać getMimeEncoder())
//            String base64 = Base64.getEncoder().encodeToString(dl.bytes);
//
//            // 4) Zbuduj dataURL
//            String dataUrl = "data:" + mime + ";base64," + base64;
//
//            // (opcjonalnie) przytnij do 2 MiB łącznie z prefiksem
//            int maxBytes = 2 * 1024 * 1024; // 2 MiB
//            dataUrl = truncateDataUrlToMaxBytes(dataUrl, maxBytes);
//            return dataUrl;
//        } catch (IOException e) {
//            return "";
//        }
//    }
//
//    static class DownloadResult {
//        byte[] bytes;
//        String contentType;
//    }
//
//    private static DownloadResult download(String urlStr) throws IOException {
//        URL url = new URL(urlStr);
//        HttpURLConnection c = (HttpURLConnection) url.openConnection();
//        c.setInstanceFollowRedirects(true);
//        c.setRequestProperty("User-Agent", "Java");
//        c.setRequestProperty("Accept", "*/*");
//
//        int code = c.getResponseCode();
//        if (code / 100 == 3) { // manualny redirect, jeśli potrzeba
//            String loc = c.getHeaderField("Location");
//            if (loc != null) return download(loc);
//        }
//
//        String contentType = c.getContentType(); // np. image/jpeg
//        InputStream in = c.getInputStream();
//
//        // Obsłuż gzip, jeśli serwer go użył (rzadko dla obrazów, częściej dla SVG)
//        String enc = c.getContentEncoding();
//        if (enc != null && enc.toLowerCase().contains("gzip")) {
//            in = new java.util.zip.GZIPInputStream(in);
//        }
//
//        byte[] bytes = readAll(in);
//        DownloadResult r = new DownloadResult();
//        r.bytes = bytes;
//        r.contentType = contentType;
//        return r;
//    }
//
//    private static byte[] readAll(InputStream in) throws IOException {
//        try (in; ByteArrayOutputStream bos = new ByteArrayOutputStream(32_768)) {
//            byte[] buf = new byte[16_384];
//            int n;
//            while ((n = in.read(buf)) >= 0) bos.write(buf, 0, n);
//            return bos.toByteArray();
//        }
//    }
//
//    private static String sniffMime(byte[] b) {
//        if (b.length >= 2 && b[0] == (byte)0xFF && b[1] == (byte)0xD8) return "image/jpeg";               // JPEG
//        if (b.length >= 8 &&
//                b[0]==(byte)0x89 && b[1]==0x50 && b[2]==0x4E && b[3]==0x47 && b[4]==0x0D && b[5]==0x0A && b[6]==0x1A && b[7]==0x0A)
//            return "image/png";                                                                           // PNG
//        if (b.length >= 6) {
//            String s = new String(b, 0, 6);
//            if (s.equals("GIF87a") || s.equals("GIF89a")) return "image/gif";                             // GIF
//        }
//        if (b.length >= 12) {
//            String riff = new String(b, 0, 4);
//            String webp = new String(b, 8, 4);
//            if ("RIFF".equals(riff) && "WEBP".equals(webp)) return "image/webp";                          // WEBP
//        }
//        if (b.length >= 2 && b[0]=='B' && b[1]=='M') return "image/bmp";                                  // BMP
//        // SVG: pomiń BOM i whitespace i szukaj "<svg"
//        int i = 0;
//        while (i < b.length && Character.isWhitespace((char)(b[i] & 0xFF))) i++;
//        if (i + 4 <= b.length) {
//            String maybe = new String(b, i, Math.min(5, b.length - i)).toLowerCase();
//            if (maybe.startsWith("<svg")) return "image/svg+xml";
//        }
//        return null;
//    }
//
//    // Bezpieczne przycinanie do rozmiaru (wielokrotność 4 dla Base64)
//    private static String truncateDataUrlToMaxBytes(String dataUrl, int maxBytes) {
//        if (dataUrl.getBytes().length <= maxBytes) return dataUrl;
//
//        int comma = dataUrl.indexOf(',');
//        if (comma < 0) return dataUrl; // nieoczekiwany format
//
//        String prefix = dataUrl.substring(0, comma + 1);
//        String b64 = dataUrl.substring(comma + 1);
//
//        int maxB64Bytes = maxBytes - prefix.getBytes().length;
//        if (maxB64Bytes <= 0) return prefix; // tylko prefiks się zmieści
//
//        // Base64 musi być długości wielokrotności 4
//        int safeLen = Math.max(0, (maxB64Bytes / 4) * 4);
//        if (safeLen < b64.length()) {
//            b64 = b64.substring(0, safeLen);
//        }
//        return prefix + b64;
//    }
//}
