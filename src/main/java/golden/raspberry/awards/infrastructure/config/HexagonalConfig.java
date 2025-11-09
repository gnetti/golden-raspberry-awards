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
import golden.raspberry.awards.core.application.usecase.CalculateIntervalsUseCaseImpl;
import golden.raspberry.awards.core.application.usecase.CreateMovieUseCaseImpl;
import golden.raspberry.awards.core.application.usecase.DeleteMovieUseCaseImpl;
import golden.raspberry.awards.core.application.usecase.GetMovieUseCaseImpl;
import golden.raspberry.awards.core.application.usecase.ListenerOperationUseCase;
import golden.raspberry.awards.core.application.usecase.UpdateMovieUseCaseImpl;
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
     * Creates a bean for the ListenerOperationUseCase.
     *
     * <p>This bean wires the application use case (ListenerOperationUseCase) with
     * the listener adapter (ListenerPort). The adapter is automatically
     * injected by Spring, maintaining the hexagonal architecture pattern.
     *
     * <p><strong>Flow:</strong>
     * <pre>
     * Application → ListenerOperationUseCase (this bean)
     *                                    ↓
     *                    ListenerPort (adapter implementation)
     *                                    ↓
     *                    FileListenerAdapter (file adapter)
     *                                    ↓
     *                    File System (resources/listener)
     * </pre>
     *
     * @param listenerPort Listener port (automatically injected by Spring)
     * @return ListenerOperationUseCase bean
     * @throws NullPointerException if listenerPort is null
     */
    @Bean
    public ListenerOperationUseCase listenerOperationUseCase(ListenerPort listenerPort) {
        Objects.requireNonNull(listenerPort, "ListenerPort cannot be null");
        return new ListenerOperationUseCase(listenerPort);
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
        return new CreateMovieUseCaseImpl(saveMovieWithIdPort, idKeyManagerPort, csvFileWriterPort);
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
        return new GetMovieUseCaseImpl(repository, getMovieWithIdPort);
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
        return new UpdateMovieUseCaseImpl(repository, getMovieWithIdPort, saveMovieWithIdPort, csvFileWriterPort);
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
        return new DeleteMovieUseCaseImpl(repository, csvFileWriterPort);
    }
}

