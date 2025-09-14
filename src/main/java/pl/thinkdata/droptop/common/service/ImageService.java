package pl.thinkdata.droptop.common.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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

            URL url = new URL(cleanUrl);
            URLConnection connection = url.openConnection();
            String contentType = connection.getContentType();
            String extension = getExtensionFromContentType(contentType);
            Path filePath = Paths.get(directory).resolve(dir).resolve(ean + "." + extension);

            InputStream in = new URL(cleanUrl).openStream();
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        } catch (IOException e) {
            return "dupa";
        }
    }

    private String generateFileLogoName(String fileName, String ean) {
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        return ean  + fileExtension;
    }

    private String getExtensionFromContentType(String contentType) {
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
