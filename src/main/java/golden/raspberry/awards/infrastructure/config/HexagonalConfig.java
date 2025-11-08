package golden.raspberry.awards.infrastructure.config;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.application.port.out.LoggingPort;
import golden.raspberry.awards.core.application.usecase.CalculateIntervalsUseCaseImpl;
import golden.raspberry.awards.core.application.usecase.LogOperationUseCase;
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
     * Creates a bean for the ProducerIntervalService (Domain Service).
     *
     * <p>This bean wires the domain service with the repository adapter.
     * The adapter is automatically injected by Spring, maintaining the
     * hexagonal architecture pattern.
     *
     * @param repository Movie repository port (automatically injected by Spring)
     * @return ProducerIntervalService bean
     * @throws NullPointerException if repository is null
     */
    @Bean
    public ProducerIntervalService producerIntervalService(MovieRepositoryPort repository) {
        Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        return new ProducerIntervalService(repository);
    }
    
    /**
     * Creates a bean for the CalculateIntervalsUseCase.
     *
     * <p>This bean wires the application use case (CalculateIntervalsUseCaseImpl) with
     * the domain service (ProducerIntervalService). The domain service is automatically
     * injected by Spring, maintaining the hexagonal architecture pattern.
     *
     * <p><strong>Flow:</strong>
     * <pre>
     * REST Controller → CalculateIntervalsUseCase (this bean)
     *                                    ↓
     *                    CalculateIntervalsUseCaseImpl (Application - Use Case)
     *                                    ↓
     *                    ProducerIntervalService (Domain - Service)
     *                                    ↓
     *                    MovieRepositoryPort (Domain - Port OUT)
     *                                    ↓
     *                    MovieRepositoryAdapter (Infrastructure - Adapter OUT)
     * </pre>
     *
     * @param producerIntervalService Domain service (automatically injected by Spring)
     * @return CalculateIntervalsUseCase bean
     * @throws NullPointerException if producerIntervalService is null
     */
    @Bean
    public CalculateIntervalsUseCase calculateIntervalsUseCase(
            ProducerIntervalService producerIntervalService) {
        
        Objects.requireNonNull(producerIntervalService, "ProducerIntervalService cannot be null");
        return new CalculateIntervalsUseCaseImpl(producerIntervalService);
    }
    
    /**
     * Creates a bean for the LogOperationUseCase.
     *
     * <p>This bean wires the application use case (LogOperationUseCase) with
     * the logging adapter (LoggingPort). The adapter is automatically
     * injected by Spring, maintaining the hexagonal architecture pattern.
     *
     * <p><strong>Flow:</strong>
     * <pre>
     * Application → LogOperationUseCase (this bean)
     *                                    ↓
     *                    LoggingPort (adapter implementation)
     *                                    ↓
     *                    FileLoggingAdapter (file adapter)
     *                                    ↓
     *                    File System (resources/log)
     * </pre>
     *
     * @param loggingPort Logging port (automatically injected by Spring)
     * @return LogOperationUseCase bean
     * @throws NullPointerException if loggingPort is null
     */
    @Bean
    public LogOperationUseCase logOperationUseCase(LoggingPort loggingPort) {
        Objects.requireNonNull(loggingPort, "LoggingPort cannot be null");
        return new LogOperationUseCase(loggingPort);
    }
}

