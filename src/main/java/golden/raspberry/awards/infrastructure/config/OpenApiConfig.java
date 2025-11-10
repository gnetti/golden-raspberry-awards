package golden.raspberry.awards.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Golden Raspberry Awards API")
                        .version("1.0.0")
                        .description("RestFul API for reading the list of nominees and winners of the Worst Movie category of the Golden Raspberry Awards")
                        .contact(new Contact()
                                .name("Luiz Generoso")));
    }
}

