package pl.thinkdata.droptop.common.service;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Service
public class ImageService {
    private String directory;

    public ImageService(@Value("${file.directory}") String directory) {
        this.directory = directory;
    }

    public String uploadImage(String file, String dir, String ean) {
        try {
            String cleanUrl = StringEscapeUtils.unescapeHtml4(file);
            String extension = getExtensionFromContentType(cleanUrl);
            Path filePath = Paths.get(directory).resolve(dir).resolve(ean + "." + extension);
            Files.createDirectories(filePath.getParent());
            InputStream in = new URL(cleanUrl).openStream();
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.getFileName().toString();
        } catch (IOException e) {
            return "notfound.jpg";
        }
    }

    public ResponseEntity<Resource> serveImages(String filename, String dir) {
        try {
            String uploadDir = Paths.get(directory).resolve(dir).resolve(filename).toString();
            FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();
            Resource file = fileSystemResourceLoader.getResource(uploadDir);
            if (file.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Path.of(filename)))
                        .body(file);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Custom-Header", "Not found image");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .build();
        } catch (IOException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Custom-Header", "Not found image");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .build();
        }
    }

    private String getExtensionFromContentType(String cleanUrl) {
        String contentType;
        try {
            URL url = new URL(cleanUrl);
            contentType = url.openConnection().getContentType();
        } catch (IOException e) {
            contentType = null;
        }

        if (contentType == null) return "";
        Map<String, String> mapping = Map.of(
                "image/jpeg", "jpg",
                "image/jpg", "jpg",
                "image/png", "png",
                "image/gif", "gif",
                "image/webp", "webp"
        );

        return mapping.getOrDefault(contentType.toLowerCase(), "");
    }
}
