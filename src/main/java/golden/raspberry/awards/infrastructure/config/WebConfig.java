package golden.raspberry.awards.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web configuration for serving JavaDoc documentation.
 * Configures resource handlers to serve JavaDoc files from the external docs directory.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures resource handlers for JavaDoc documentation and Postman files.
     * Serves JavaDoc files from the external docs directory and Postman files from data directory.
     *
     * @param registry ResourceHandlerRegistry to configure
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        var docsDir = new File("docs").getAbsolutePath();
        var docsPath = "file:" + docsDir.replace("\\", "/") + "/";
        
        registry.addResourceHandler("/docs/**")
                .addResourceLocations(docsPath)
                .setCachePeriod(3600)
                .resourceChain(true);
        
        var dataDir = new File("data").getAbsolutePath();
        var dataPath = "file:" + dataDir.replace("\\", "/") + "/";
        
        registry.addResourceHandler("/data/**")
                .addResourceLocations(dataPath)
                .setCachePeriod(3600)
                .resourceChain(true);
    }
}

