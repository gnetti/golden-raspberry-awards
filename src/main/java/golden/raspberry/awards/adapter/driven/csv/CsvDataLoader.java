package golden.raspberry.awards.adapter.driven.csv;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * CSV Data Loader for populating database on application startup.
 *
 * <p>This component implements CommandLineRunner to execute CSV loading
 * automatically when the application starts. It reads the CSV file from
 * the classpath and populates the database via MovieRepositoryPort.
 *
 * <p><strong>Important:</strong>
 * <ul>
 *   <li>CSV is the <strong>sole data source</strong> of the project</li>
 *   <li>Database schema is already created by JPA (ddl-auto=create-drop)</li>
 *   <li>This loader only <strong>populates</strong> the database with CSV data</li>
 *   <li>No SQL files or migrations are needed</li>
 * </ul>
 *
 * <p>Uses Java 21 features elegantly and robustly:
 * <ul>
 *   <li>Streams API for functional processing</li>
 *   <li>Pattern Matching for validation</li>
 *   <li>Switch Expressions for field parsing</li>
 *   <li>Text Blocks for complex messages</li>
 *   <li>Records for internal data structures</li>
 *   <li>String Templates for formatted messages</li>
 *   <li>Method references for cleaner code</li>
 * </ul>
 *
 * @author Golden Raspberry Awards Team
 * @since 1.0.0
 */
@Component
public class CsvDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataLoader.class);
    private static final String CSV_FILE = "data/movieList.csv";
    private static final char CSV_SEPARATOR = ';';
    private static final int MIN_COLUMNS = 5;
    private static final String WINNER_YES = "yes";

    private final MovieRepositoryPort repository;

    /**
     * Constructor for dependency injection.
     *
     * @param repository Movie repository port for saving movies
     */
    public CsvDataLoader(MovieRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
    }

    /**
     * Executes CSV data loading on application startup.
     *
     * <p>This method is automatically called by Spring Boot after
     * the application context is fully initialized.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        logger.info("""
                ========================================
                Starting CSV Data Load
                ========================================
                File: {}
                Database: H2 (in-memory)
                Schema: Auto-created by JPA (ddl-auto=create-drop)
                Data Source: CSV (sole source - no SQL files)
                ========================================
                """, CSV_FILE);

        try {
            var movies = loadMoviesFromCsv();
            repository.saveAll(movies);

            logger.info("""
                    ========================================
                    CSV Data Load Completed Successfully
                    ========================================
                    Total movies loaded: {}
                    ========================================
                    """, movies.size());
        } catch (Exception e) {
            logger.error("""
                    ========================================
                    CSV Data Load Failed
                    ========================================
                    Error: {}
                    ========================================
                    """, e.getMessage(), e);
            throw new RuntimeException("Failed to load CSV data: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * Loads movies from CSV file using Java 21 Streams API.
     *
     * <p>Reads the CSV file from classpath, parses each line using streams,
     * and converts them to Movie domain models. Invalid lines are skipped
     * with appropriate warnings.
     *
     * @return List of Movie domain models parsed from CSV
     * @throws Exception if CSV file cannot be read or parsed
     */
    private List<Movie> loadMoviesFromCsv() throws Exception {
        var resource = new ClassPathResource(CSV_FILE);

        if (!resource.exists()) {
            throw new IllegalStateException("CSV file not found: %s".formatted(CSV_FILE));
        }

        var parser = new CSVParserBuilder()
                .withSeparator(CSV_SEPARATOR)
                .build();

        try (var reader = new CSVReaderBuilder(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .withCSVParser(parser)
                .build()) {

            var allLines = reader.readAll();

            if (allLines.isEmpty()) {
                logger.warn("CSV file is empty: {}", CSV_FILE);
                return List.of();
            }

            return IntStream.range(0, allLines.size())
                    .skip(1)
                    .mapToObj(index -> parseLineSafely(allLines.get(index), index + 1))
                    .filter(Objects::nonNull)
                    .toList();
        }
    }

    /**
     * Safely parses a CSV line, returning null if parsing fails.
     * Uses pattern matching and switch expressions for elegant error handling.
     *
     * @param line       CSV line as string array
     * @param lineNumber Line number for error reporting (1-based)
     * @return Movie domain model, or null if parsing fails
     */
    private Movie parseLineSafely(String[] line, int lineNumber) {
        var validationResult = validateLine(line, lineNumber);

        return switch (validationResult) {
            case ValidationResult.Valid valid -> {
                try {
                    yield parseMovieLine(valid.line(), valid.lineNumber());
                } catch (Exception e) {
                    logger.warn("""
                            Skipping invalid line {}:
                              Reason: {}
                              Data: {}
                            """, lineNumber, e.getMessage(), String.join(";", line));
                    yield null;
                }
            }
            case ValidationResult.Invalid invalid -> {
                logger.warn("""
                                Skipping invalid line {}:
                                  Reason: {}
                                  Expected columns: {}
                                  Got: {}
                                """,
                        invalid.lineNumber(),
                        invalid.reason(),
                        MIN_COLUMNS,
                        invalid.actualColumns());
                yield null;
            }
        };
    }

    /**
     * Validates a CSV line using pattern matching.
     *
     * @param line       CSV line to validate
     * @param lineNumber Line number for error reporting
     * @return ValidationResult (sealed interface pattern)
     */
    private ValidationResult validateLine(String[] line, int lineNumber) {
        if (line == null) {
            return new ValidationResult.Invalid(lineNumber, "Line is null", 0);
        }

        if (line.length < MIN_COLUMNS) {
            return new ValidationResult.Invalid(lineNumber, "Insufficient columns", line.length);
        }

        return new ValidationResult.Valid(line, lineNumber);
    }

    /**
     * Parses a validated CSV line into a Movie domain model.
     * Uses switch expressions for field extraction and validation.
     *
     * @param line       CSV line as string array (guaranteed valid)
     * @param lineNumber Line number for error reporting
     * @return Movie domain model
     * @throws IllegalArgumentException if line cannot be parsed
     */
    private Movie parseMovieLine(String[] line, int lineNumber) {
        return new Movie(
                parseYear(line[0], lineNumber),
                parseField(line[1], "Title", lineNumber),
                parseField(line[2], "Studios", lineNumber),
                parseField(line[3], "Producers", lineNumber),
                parseWinner(line[4])
        );
    }

    /**
     * Parses year field with robust validation using String Templates.
     *
     * @param value      String value to parse
     * @param lineNumber Line number for error reporting
     * @return Parsed integer value
     * @throws IllegalArgumentException if value is invalid
     */
    private Integer parseYear(String value, int lineNumber) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Line %d: Year cannot be empty".formatted(lineNumber));
        }

        try {
            var year = Integer.parseInt(value.trim());
            if (year < 1900 || year > 2100) {
                throw new IllegalArgumentException(
                        "Line %d: Year out of valid range (1900-2100): %d"
                                .formatted(lineNumber, year));
            }
            return year;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Line %d: Invalid year format: %s"
                            .formatted(lineNumber, value), e);
        }
    }

    /**
     * Parses a string field with trimming and validation.
     *
     * @param value      String value to parse
     * @param fieldName  Name of the field for error messages
     * @param lineNumber Line number for error reporting
     * @return Trimmed string value
     * @throws IllegalArgumentException if value is invalid
     */
    private String parseField(String value, String fieldName, int lineNumber) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Line %d: %s cannot be null".formatted(lineNumber, fieldName));
        }

        var trimmed = value.trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException(
                    "Line %d: %s cannot be blank".formatted(lineNumber, fieldName));
        }

        return trimmed;
    }

    /**
     * Parses winner field using pattern matching.
     *
     * @param value String value to parse
     * @return Boolean indicating if movie is a winner
     */
    private Boolean parseWinner(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return WINNER_YES.equalsIgnoreCase(value.trim());
    }

    /**
     * Sealed interface for validation results using pattern matching.
     * Provides type-safe validation results.
     */
    private sealed interface ValidationResult {
        /**
         * Valid line with parsed data.
         *
         * @param line       Valid CSV line
         * @param lineNumber Line number (1-based)
         */
        record Valid(String[] line, int lineNumber) implements ValidationResult {
        }

        /**
         * Invalid line with error information.
         *
         * @param lineNumber    Line number (1-based)
         * @param reason        Reason for invalidity
         * @param actualColumns Actual number of columns found
         */
        record Invalid(int lineNumber, String reason, int actualColumns) implements ValidationResult {
        }
    }
}
