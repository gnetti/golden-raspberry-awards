package golden.raspberry.awards.infrastructure.config;

import golden.raspberry.awards.core.domain.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;
import golden.raspberry.awards.core.domain.service.ProducerIntervalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Hexagonal Architecture Configuration.
 *
 * <p>This configuration class connects the Domain layer with Infrastructure adapters
 * using Spring's dependency injection. It creates beans for domain services,
 * maintaining the purity of the domain layer (no Spring annotations in domain).
 *
 * <p><strong>Hexagonal Architecture Principles:</strong>
 * <ul>
 *   <li>Domain layer remains pure (no Spring dependencies)</li>
 *   <li>Configuration layer wires domain services with adapters</li>
 *   <li>Dependency Inversion: Domain defines ports, Infrastructure implements them</li>
 *   <li>Dependencies point inward: Infrastructure → Application → Domain</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, var, Objects.requireNonNull for validation.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Configuration
public class HexagonalConfig {

    /**
     * Creates a bean for the CalculateIntervalsUseCase.
     *
     * <p>This bean wires the domain service (ProducerIntervalService) with
     * the repository adapter (MovieRepositoryPort). The adapter is automatically
     * injected by Spring, maintaining the hexagonal architecture pattern.
     *
     * <p><strong>Flow:</strong>
     * <pre>
     * REST Controller → CalculateIntervalsUseCase (this bean)
     *                                    ↓
     *                    ProducerIntervalService (domain service)
     *                                    ↓
     *                    MovieRepositoryPort (adapter implementation)
     *                                    ↓
     *                    MovieRepositoryAdapter (JPA adapter)
     * </pre>
     *
     * @param repository Movie repository port (automatically injected by Spring)
     * @return CalculateIntervalsUseCase bean
     * @throws NullPointerException if repository is null
     */
    @Bean
    public CalculateIntervalsUseCase calculateIntervalsUseCase(
            MovieRepositoryPort repository) {
        
        Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        
        return new ProducerIntervalService(repository);
    }
}

