package golden.raspberry.awards.infrastructure.config;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.application.port.in.CreateMovieUseCase;
import golden.raspberry.awards.core.application.port.in.DeleteMovieUseCase;
import golden.raspberry.awards.core.application.port.in.GetMovieUseCase;
import golden.raspberry.awards.core.application.port.in.UpdateMovieUseCase;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.GetMovieWithIdPort;
import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
import golden.raspberry.awards.core.application.port.out.ListenerPort;
import golden.raspberry.awards.core.application.port.out.SaveMovieWithIdPort;
import golden.raspberry.awards.core.application.service.ProducerIntervalCalculationService;
import golden.raspberry.awards.core.application.usecase.CalculateIntervalsUseCaseHandler;
import golden.raspberry.awards.core.application.usecase.CreateMovieUseCaseHandler;
import golden.raspberry.awards.core.application.usecase.DeleteMovieUseCaseHandler;
import golden.raspberry.awards.core.application.usecase.GetMovieUseCaseHandler;
import golden.raspberry.awards.core.application.usecase.UpdateMovieUseCaseHandler;
import golden.raspberry.awards.core.domain.repository.MovieRepositoryPort;
import golden.raspberry.awards.core.domain.service.ProducerIntervalCalculator;
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
     * Creates a bean for the ProducerIntervalCalculator domain service.
     *
     * <p>This bean creates the domain service that contains ALL business rules
     * for interval calculations, following hexagonal architecture principles.
     *
     * @return ProducerIntervalCalculator bean
     */
    @Bean
    public ProducerIntervalCalculator producerIntervalCalculator() {
        return new ProducerIntervalCalculator();
    }

    /**
     * Creates a bean for the ProducerIntervalCalculationService application service.
     *
     * <p>This bean wires the application service that orchestrates interval calculations
     * by coordinating repository calls and domain service calls.
     *
     * <p><strong>Flow:</strong>
     * <pre>
     * Application Service (this bean)
     *     ↓
     * MovieRepositoryPort (Domain - Port OUT)
     *     ↓
     * ProducerIntervalCalculator (Domain - Service)
     * </pre>
     *
     * @param repository Movie repository port (automatically injected by Spring)
     * @param calculator Domain service for interval calculations (automatically injected by Spring)
     * @return ProducerIntervalCalculationService bean
     * @throws NullPointerException if repository or calculator is null
     */
    @Bean
    public ProducerIntervalCalculationService producerIntervalCalculationService(
            MovieRepositoryPort repository,
            ProducerIntervalCalculator calculator) {
        
        Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        Objects.requireNonNull(calculator, "ProducerIntervalCalculator cannot be null");
        return new ProducerIntervalCalculationService(repository, calculator);
    }

    /**
     * Creates a bean for the CalculateIntervalsUseCase.
     *
     * <p>This bean wires the application use case handler (CalculateIntervalsUseCaseHandler) with
     * the application service. The handler delegates to the service,
     * following hexagonal architecture principles.
     *
     * <p><strong>Flow:</strong>
     * <pre>
     * REST Controller → CalculateIntervalsUseCase (this bean)
     *                                    ↓
     *                    CalculateIntervalsUseCaseHandler (Application - Handler)
     *                                    ↓
     *                    ProducerIntervalCalculationService (Application - Service)
     *                                    ↓
     *                    ProducerIntervalCalculator (Domain - Service)
     * </pre>
     *
     * @param calculationService Application service for orchestrating interval calculations (automatically injected by Spring)
     * @return CalculateIntervalsUseCase bean
     * @throws NullPointerException if calculationService is null
     */
    @Bean
    public CalculateIntervalsUseCase calculateIntervalsUseCase(
            ProducerIntervalCalculationService calculationService) {
        
        Objects.requireNonNull(calculationService, "ProducerIntervalCalculationService cannot be null");
        return new CalculateIntervalsUseCaseHandler(calculationService);
    }
    

    /**
     * Creates a bean for the CreateMovieUseCase.
     *
     * @param saveMovieWithIdPort Port for saving movie with specific ID (automatically injected by Spring)
     * @param idKeyManagerPort    Port for managing ID keys in XML (automatically injected by Spring)
     * @param csvFileWriterPort   Port for writing movies to CSV file (automatically injected by Spring)
     * @return CreateMovieUseCase bean
     */
    @Bean
    public CreateMovieUseCase createMovieUseCase(
            SaveMovieWithIdPort saveMovieWithIdPort,
            IdKeyManagerPort idKeyManagerPort,
            CsvFileWriterPort csvFileWriterPort) {
        Objects.requireNonNull(saveMovieWithIdPort, "SaveMovieWithIdPort cannot be null");
        Objects.requireNonNull(idKeyManagerPort, "IdKeyManagerPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
        return new CreateMovieUseCaseHandler(saveMovieWithIdPort, idKeyManagerPort, csvFileWriterPort);
    }

    /**
     * Creates a bean for the GetMovieUseCase.
     *
     * @param repository        Movie repository port (automatically injected by Spring)
     * @param getMovieWithIdPort Port for getting movie with ID (automatically injected by Spring)
     * @return GetMovieUseCase bean
     */
    @Bean
    public GetMovieUseCase getMovieUseCase(
            MovieRepositoryPort repository,
            GetMovieWithIdPort getMovieWithIdPort) {
        Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        Objects.requireNonNull(getMovieWithIdPort, "GetMovieWithIdPort cannot be null");
        return new GetMovieUseCaseHandler(repository, getMovieWithIdPort);
    }

    /**
     * Creates a bean for the UpdateMovieUseCase.
     *
     * @param repository        Movie repository port (automatically injected by Spring)
     * @param getMovieWithIdPort Port for getting movie with ID (automatically injected by Spring)
     * @param saveMovieWithIdPort Port for saving movie with specific ID (automatically injected by Spring)
     * @param csvFileWriterPort Port for writing movies to CSV file (automatically injected by Spring)
     * @return UpdateMovieUseCase bean
     */
    @Bean
    public UpdateMovieUseCase updateMovieUseCase(
            MovieRepositoryPort repository,
            GetMovieWithIdPort getMovieWithIdPort,
            SaveMovieWithIdPort saveMovieWithIdPort,
            CsvFileWriterPort csvFileWriterPort) {
        Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        Objects.requireNonNull(getMovieWithIdPort, "GetMovieWithIdPort cannot be null");
        Objects.requireNonNull(saveMovieWithIdPort, "SaveMovieWithIdPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
        return new UpdateMovieUseCaseHandler(repository, getMovieWithIdPort, saveMovieWithIdPort, csvFileWriterPort);
    }

    /**
     * Creates a bean for the DeleteMovieUseCase.
     *
     * @param repository      Movie repository port (automatically injected by Spring)
     * @param csvFileWriterPort Port for writing movies to CSV file (automatically injected by Spring)
     * @return DeleteMovieUseCase bean
     */
    @Bean
    public DeleteMovieUseCase deleteMovieUseCase(
            MovieRepositoryPort repository,
            CsvFileWriterPort csvFileWriterPort) {
        Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
        return new DeleteMovieUseCaseHandler(repository, csvFileWriterPort);
    }
}

