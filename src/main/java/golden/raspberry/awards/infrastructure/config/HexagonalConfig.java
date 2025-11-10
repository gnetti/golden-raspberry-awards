package golden.raspberry.awards.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Main Hexagonal Architecture Configuration.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Configuration
@Import({
    HexagonalBeansConfig.class,
    PortsConfiguration.class
})
public class HexagonalConfig {
}

