package pl.thinkdata.droptop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Profile("dev")
@Configuration
public class ResourcesConfigurationForDevProfile implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //Setting for widows 10
        Path imagesDirection = Paths.get("data");
        String imagesPath = imagesDirection.toFile().getAbsolutePath();
        registry.addResourceHandler("/data/**").addResourceLocations("file:/" + imagesPath + "/");
    }

}