package golden.raspberry.awards.infrastructure.config;

import golden.raspberry.awards.core.domain.service.IntervalProcessorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Hexagonal Architecture beans.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Configuration
public class HexagonalBeansConfig {

    /**
     * Creates a bean for the IntervalProcessorService domain service.
     *
     * @return IntervalProcessorService bean
     */
    @Bean
    public IntervalProcessorService producerIntervalCalculator() {
        return new IntervalProcessorService();
    }
}

