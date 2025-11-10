package golden.raspberry.awards.infrastructure.config;

import golden.raspberry.awards.core.application.port.in.*;
import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
import golden.raspberry.awards.core.application.port.out.MovieQueryPort;
import golden.raspberry.awards.core.application.port.out.SaveMovieWithIdPort;
import golden.raspberry.awards.core.application.service.IntervalProcessorService;
import golden.raspberry.awards.core.application.usecase.*;
import golden.raspberry.awards.core.application.usecase.CalculateIntervalsPortHandler;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidator;
import golden.raspberry.awards.core.application.port.out.MovieRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Configuration for Ports and Use Cases wiring.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Configuration
public class PortsConfiguration {

    /**
     * Creates a bean for the IntervalProcessorService application service.
     *
     * @param repository Movie repository port
     * @param calculator Domain service for interval calculations
     * @return IntervalProcessorService bean
     */
    @Bean
    public IntervalProcessorService producerIntervalCalculationService(
            MovieRepositoryPort repository,
            golden.raspberry.awards.core.domain.service.IntervalProcessorService calculator) {
        
        Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        Objects.requireNonNull(calculator, "IntervalProcessorService cannot be null");
        return new IntervalProcessorService(repository, calculator);
    }

    /**
     * Creates a bean for the CalculateIntervalsPort.
     *
     * @param calculationService Application service for orchestrating interval calculations
     * @return CalculateIntervalsPort bean
     */
    @Bean
    public CalculateIntervalsPort calculateIntervalsUseCase(
            IntervalProcessorService calculationService) {
        
        Objects.requireNonNull(calculationService, "IntervalProcessorService cannot be null");
        return new CalculateIntervalsPortHandler(calculationService);
    }

    /**
     * Creates a bean for the CreateMoviePort.
     *
     * @param saveMovieWithIdPort Port for saving movie with specific ID
     * @param idKeyManagerPort    Port for managing ID keys in XML
     * @param csvFileWriterPort   Port for writing movies to CSV file
     * @return CreateMoviePort bean
     */
    @Bean
    public CreateMoviePort createMovieUseCase(
            SaveMovieWithIdPort saveMovieWithIdPort,
            IdKeyManagerPort idKeyManagerPort,
            CsvFileWriterPort csvFileWriterPort) {
        Objects.requireNonNull(saveMovieWithIdPort, "SaveMovieWithIdPort cannot be null");
        Objects.requireNonNull(idKeyManagerPort, "IdKeyManagerPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
        return new CreateMoviePortHandler(saveMovieWithIdPort, idKeyManagerPort, csvFileWriterPort);
    }

    /**
     * Creates a bean for the GetMoviePort.
     *
     * @param movieQueryPort Port for querying movies
     * @return GetMoviePort bean
     */
    @Bean
    public GetMoviePort getMovieUseCase(MovieQueryPort movieQueryPort) {
        Objects.requireNonNull(movieQueryPort, "MovieQueryPort cannot be null");
        return new GetMoviePortHandler(movieQueryPort);
    }

    /**
     * Creates a bean for the MovieValidator.
     *
     * @param movieQueryPort Port for querying movies
     * @return MovieValidator bean
     */
    @Bean
    public MovieValidator movieValidator(MovieQueryPort movieQueryPort) {
        Objects.requireNonNull(movieQueryPort, "MovieQueryPort cannot be null");
        return new MovieValidator(movieQueryPort);
    }

    /**
     * Creates a bean for the UpdateMoviePort.
     *
     * @param validator           Movie validator
     * @param saveMovieWithIdPort Port for saving movie with specific ID
     * @param csvFileWriterPort   Port for writing movies to CSV file
     * @return UpdateMoviePort bean
     */
    @Bean
    public UpdateMoviePort updateMovieUseCase(
            MovieValidator validator,
            SaveMovieWithIdPort saveMovieWithIdPort,
            CsvFileWriterPort csvFileWriterPort) {
        Objects.requireNonNull(validator, "MovieValidator cannot be null");
        Objects.requireNonNull(saveMovieWithIdPort, "SaveMovieWithIdPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
        return new UpdateMoviePortHandler(validator, saveMovieWithIdPort, csvFileWriterPort);
    }

    /**
     * Creates a bean for the DeleteMoviePort.
     *
     * @param validator          Movie validator
     * @param repository         Movie repository port
     * @param csvFileWriterPort  Port for writing movies to CSV file
     * @return DeleteMoviePort bean
     */
    @Bean
    public DeleteMoviePort deleteMovieUseCase(
            MovieValidator validator,
            MovieRepositoryPort repository,
            CsvFileWriterPort csvFileWriterPort) {
        Objects.requireNonNull(validator, "MovieValidator cannot be null");
        Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
        return new DeleteMoviePortHandler(validator, repository, csvFileWriterPort);
    }
}

