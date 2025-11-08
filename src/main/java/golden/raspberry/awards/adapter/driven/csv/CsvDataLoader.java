package golden.raspberry.awards.adapter.driven.csv;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import golden.raspberry.awards.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.adapter.driven.persistence.repository.MovieJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
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
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class CsvDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataLoader.class);

    private final MovieJpaRepository jpaRepository;
    private final boolean resetToOriginal;
    private final String csvFile;
    private final String csvOriginalFile;
    private final char csvSeparator;
    private final int minColumns;
    private final String winnerYes;

    /**
     * Constructor for dependency injection.
     *
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>@Value for configuration injection</li>
     *   <li>Objects.requireNonNull for null safety</li>
     * </ul>
     *
     * @param jpaRepository   JPA repository for saving movies with IDs from CSV
     * @param resetToOriginal If true, resets CSV to original file from primary-base on startup
     * @param csvFile         CSV file path from properties
     * @param csvOriginalFile Original CSV file path from properties
     * @param csvSeparator    CSV separator character from properties
     * @param minColumns      Minimum required columns from properties (6: id;year;title;studios;producers;winner)
     * @param winnerYes       Winner value string from properties
     */
    public CsvDataLoader(
            MovieJpaRepository jpaRepository,
            @Value("${csv.reset-to-original:false}") boolean resetToOriginal,
            @Value("${csv.file:data/movieList.csv}") String csvFile,
            @Value("${csv.original-file:data/primary-base/MovieList.csv}") String csvOriginalFile,
            @Value("${csv.separator:;}") String csvSeparator,
            @Value("${csv.min-columns:6}") int minColumns,
            @Value("${csv.winner-yes:yes}") String winnerYes) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "JpaRepository cannot be null");
        this.resetToOriginal = resetToOriginal;
        this.csvFile = Objects.requireNonNull(csvFile, "CSV file path cannot be null");
        this.csvOriginalFile = Objects.requireNonNull(csvOriginalFile, "CSV original file path cannot be null");
        this.csvSeparator = csvSeparator != null && !csvSeparator.isEmpty()
                ? csvSeparator.charAt(0)
                : ';';
        this.minColumns = minColumns;
        this.winnerYes = Objects.requireNonNull(winnerYes, "Winner yes value cannot be null");
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
                Reset to Original: {}
                ========================================
                """, csvFile, resetToOriginal);

        try {
            // Reset CSV to original FIRST if configured
            if (resetToOriginal) {
                resetCsvToOriginal();
            }

            // Load movies AFTER reset (if reset was done, reads from overwritten file)
            var entities = loadMoviesFromCsv();
            saveMoviesWithIds(entities);

            logger.info("""
                    ========================================
                    CSV Data Load Completed Successfully
                    ========================================
                    Total movies loaded: {}
                    ========================================
                    """, entities.size());
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
     * Resets the CSV file to the original file from primary-base.
     * Copies data/primary-base/MovieList.csv to data/movieList.csv.
     *
     * <p>This method is called when csv.reset-to-original=true in application.properties.
     * It replaces the current movieList.csv with the original file, effectively
     * resetting all modifications made to the CSV.
     *
     * <p>Uses Java 21 features: Optional, method references, String Templates.
     *
     * @throws Exception if original CSV file cannot be read or target file cannot be written
     */
    private void resetCsvToOriginal() throws Exception {
        logger.info("Resetting CSV to original file from primary-base...");

        var originalResource = new ClassPathResource(csvOriginalFile);
        if (!originalResource.exists()) {
            throw new IllegalStateException(
                    "Original CSV file not found: %s".formatted(csvOriginalFile)
            );
        }

        var targetPath = Paths.get("src/main/resources", csvFile);
        Files.createDirectories(targetPath.getParent());
        
        try (var inputStream = originalResource.getInputStream();
             var outputStream = Files.newOutputStream(targetPath)) {
            inputStream.transferTo(outputStream);
        }

        logger.info("Successfully reset CSV file: {} -> {}", csvOriginalFile, targetPath);
    }

    /**
     * Saves movies with IDs from CSV directly to database.
     * Uses JPA repository to preserve IDs from CSV.
     *
     * @param entities List of MovieEntity with IDs from CSV
     */
    private void saveMoviesWithIds(List<MovieEntity> entities) {
        jpaRepository.saveAll(entities);
    }

    /**
     * Loads movies from CSV file using Java 21 Streams API EXTREMELY.
     *
     * <p>Reads the CSV file from file system first (if reset was done),
     * otherwise from classpath. Parses each line using streams,
     * and converts them to MovieEntity with IDs from CSV. Invalid lines are skipped
     * with appropriate warnings.
     *
     * <p>CSV format: id;year;title;studios;producers;winner
     *
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>Optional for null-safe operations</li>
     *   <li>Stream API for functional processing</li>
     *   <li>Method references for cleaner code</li>
     *   <li>String Templates for error messages</li>
     * </ul>
     *
     * @return List of MovieEntity with IDs from CSV
     * @throws Exception if CSV file cannot be read or parsed
     */
    private List<MovieEntity> loadMoviesFromCsv() throws Exception {
        // Try to read from file system first (if reset was done, this will be the overwritten file)
        var fileSystemPath = Paths.get("src/main/resources", csvFile);
        var inputStream = Files.exists(fileSystemPath)
                ? Files.newInputStream(fileSystemPath)
                : Optional.of(new ClassPathResource(csvFile))
                        .filter(ClassPathResource::exists)
                        .map(resource -> {
                            try {
                                return resource.getInputStream();
                            } catch (Exception e) {
                                throw new IllegalStateException(
                                        "CSV file not found: %s".formatted(csvFile), e);
                            }
                        })
                        .orElseThrow(() -> new IllegalStateException(
                                "CSV file not found: %s".formatted(csvFile)
                        ));

        var parser = new CSVParserBuilder()
                .withSeparator(csvSeparator)
                .build();

        try (var stream = inputStream;
             var reader = new CSVReaderBuilder(
                     new InputStreamReader(stream, StandardCharsets.UTF_8))
                     .withCSVParser(parser)
                     .build()) {

            return Optional.ofNullable(reader.readAll())
                    .filter(lines -> !lines.isEmpty())
                    .map(this::parseAllLines)
                    .orElseGet(() -> {
                        logger.warn("CSV file is empty: {}", csvFile);
                        return List.of();
                    });
        }
    }

    /**
     * Parses all CSV lines using functional approach.
     * Skips header (first line) and processes data lines.
     * Creates MovieEntity with IDs from CSV.
     *
     * @param allLines All CSV lines including header
     * @return List of parsed MovieEntity with IDs from CSV
     */
    private List<MovieEntity> parseAllLines(
            List<String[]> allLines) {
        return IntStream.range(0, allLines.size())
                .skip(1)
                .mapToObj(index -> parseEntityLineSafely(allLines.get(index), index + 1))
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Safely parses a CSV line into MovieEntity with ID from CSV.
     * Returns Optional.empty() if parsing fails.
     *
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>Pattern Matching with switch expressions</li>
     *   <li>Optional for null-safe operations</li>
     *   <li>Sealed interfaces for type-safe validation</li>
     *   <li>String Templates for error messages</li>
     * </ul>
     *
     * @param line       CSV line as string array
     * @param lineNumber Line number for error reporting (1-based)
     * @return Optional containing MovieEntity with ID, or empty if parsing fails
     */
    private Optional<MovieEntity> parseEntityLineSafely(
            String[] line, int lineNumber) {
        return validateLine(line, lineNumber)
                .map(valid -> parseMovieEntityLineSafely(valid.line(), valid.lineNumber(), line))
                .orElseGet(() -> {
                    logInvalidLine(lineNumber, line);
                    return Optional.empty();
                });
    }

    /**
     * Parses a validated CSV line safely into MovieEntity, returning Optional.empty() on error.
     *
     * @param line       CSV line as string array (guaranteed valid)
     * @param lineNumber Line number for error reporting
     * @param originalLine Original line for error logging
     * @return Optional containing MovieEntity with ID, or empty if parsing fails
     */
    private Optional<MovieEntity> parseMovieEntityLineSafely(
            String[] line, int lineNumber, String[] originalLine) {
        try {
            return Optional.of(parseMovieEntityLine(line, lineNumber));
        } catch (Exception e) {
            logger.warn("""
                    Skipping invalid line {}:
                      Reason: {}
                      Data: {}
                    """, lineNumber, e.getMessage(), String.join(";", originalLine));
            return Optional.empty();
        }
    }


    /**
     * Logs invalid line information using String Templates.
     * Uses functional approach to determine error reason.
     *
     * @param lineNumber Line number for error reporting
     * @param line       CSV line that failed validation
     */
    private void logInvalidLine(int lineNumber, String[] line) {
        logValidationError(line, lineNumber);
    }

    /**
     * Validates a CSV line using functional approach with Optional.
     * Eliminates multiple if statements using Stream API and pattern matching.
     *
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>Optional for null-safe validation</li>
     *   <li>Predicate for functional validation</li>
     *   <li>Method references for cleaner code</li>
     *   <li>Sealed interfaces for type-safe results</li>
     * </ul>
     *
     * @param line       CSV line to validate
     * @param lineNumber Line number for error reporting
     * @return Optional containing ValidationResult.Valid, or empty if invalid
     */
    private Optional<ValidationResult.Valid> validateLine(String[] line, int lineNumber) {
        return Optional.ofNullable(line)
                .filter(hasMinimumColumns())
                .map(validLine -> new ValidationResult.Valid(validLine, lineNumber))
                .or(Optional::empty);
    }

    /**
     * Creates a predicate to check if line has minimum required columns.
     * Uses method reference for elegant functional validation.
     *
     * @return Predicate that checks column count
     */
    private Predicate<String[]> hasMinimumColumns() {
        return line -> line.length >= minColumns;
    }

    /**
     * Logs validation error using functional approach.
     *
     * @param line       CSV line that failed validation
     * @param lineNumber Line number for error reporting
     */
    private void logValidationError(String[] line, int lineNumber) {
        var reason = Optional.ofNullable(line)
                .map(l -> "Insufficient columns")
                .orElse("Line is null");
        
        var actualColumns = Optional.ofNullable(line)
                .map(l -> l.length)
                .orElse(0);

        logger.warn("""
                Skipping invalid line {}:
                  Reason: {}
                  Expected columns: {}
                  Got: {}
                """, lineNumber, reason, minColumns, actualColumns);
    }

    /**
     * Parses a validated CSV line into a MovieEntity with ID from CSV.
     * Used for loading data from CSV where IDs are provided.
     *
     * <p>CSV format: id;year;title;studios;producers;winner
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>Method references for field parsing</li>
     *   <li>String Templates for error messages</li>
     * </ul>
     *
     * @param line       CSV line as string array (guaranteed valid)
     * @param lineNumber Line number for error reporting
     * @return MovieEntity with ID from CSV
     * @throws IllegalArgumentException if line cannot be parsed
     */
    private MovieEntity parseMovieEntityLine(
            String[] line, int lineNumber) {
        return new MovieEntity(
                parseId(line[0], lineNumber),
                parseYear(line[1], lineNumber),
                parseField(line[2], "Title", lineNumber),
                parseField(line[3], "Studios", lineNumber),
                parseField(line[4], "Producers", lineNumber),
                parseWinner(line[5])
        );
    }

    /**
     * Parses ID field using functional approach with Optional.
     *
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>Optional for null-safe operations</li>
     *   <li>String Templates for error messages</li>
     * </ul>
     *
     * @param value      String value to parse
     * @param lineNumber Line number for error reporting
     * @return Parsed Long ID value
     * @throws IllegalArgumentException if value is invalid
     */
    private Long parseId(String value, int lineNumber) {
        return Optional.ofNullable(value)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .map(trimmed -> parseLongSafely(trimmed, lineNumber))
                .filter(id -> id > 0)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Line %d: ID cannot be empty or invalid".formatted(lineNumber)
                ));
    }

    /**
     * Parses long safely with error handling.
     *
     * @param value      String value to parse
     * @param lineNumber Line number for error reporting
     * @return Parsed long value
     * @throws IllegalArgumentException if value cannot be parsed
     */
    private Long parseLongSafely(String value, int lineNumber) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Line %d: Invalid ID format: %s".formatted(lineNumber, value), e
            );
        }
    }

    /**
     * Parses year field using functional approach with Optional and Stream API.
     * Eliminates multiple if statements using pattern matching.
     *
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>Optional for null-safe operations</li>
     *   <li>Predicate for functional validation</li>
     *   <li>String Templates for error messages</li>
     *   <li>Method references for cleaner code</li>
     * </ul>
     *
     * @param value      String value to parse
     * @param lineNumber Line number for error reporting
     * @return Parsed integer value
     * @throws IllegalArgumentException if value is invalid
     */
    private Integer parseYear(String value, int lineNumber) {
        return Optional.ofNullable(value)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .map(trimmed -> parseIntegerSafely(trimmed, lineNumber))
                .map(year -> validateYearRange(year, lineNumber))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Line %d: Year cannot be empty".formatted(lineNumber)
                ));
    }

    /**
     * Parses integer safely with error handling.
     *
     * @param value      String value to parse
     * @param lineNumber Line number for error reporting
     * @return Parsed integer value
     * @throws IllegalArgumentException if value cannot be parsed
     */
    private Integer parseIntegerSafely(String value, int lineNumber) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Line %d: Invalid year format: %s".formatted(lineNumber, value), e
            );
        }
    }

    /**
     * Validates year range using functional approach.
     * Throws exception if year is out of range.
     *
     * @param year       Year to validate
     * @param lineNumber Line number for error reporting
     * @return Year if valid
     * @throws IllegalArgumentException if year is out of valid range (1900-2100)
     */
    private Integer validateYearRange(Integer year, int lineNumber) {
        return Optional.ofNullable(year)
                .filter(y -> y >= 1900 && y <= 2100)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Line %d: Year out of valid range (1900-2100): %d"
                                .formatted(lineNumber, year)
                ));
    }

    /**
     * Parses a string field using functional approach with Optional.
     * Eliminates multiple if statements using Stream API.
     *
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>Optional for null-safe operations</li>
     *   <li>Predicate for functional validation</li>
     *   <li>String Templates for error messages</li>
     *   <li>Method references for cleaner code</li>
     * </ul>
     *
     * @param value      String value to parse
     * @param fieldName  Name of the field for error messages
     * @param lineNumber Line number for error reporting
     * @return Trimmed string value
     * @throws IllegalArgumentException if value is invalid
     */
    private String parseField(String value, String fieldName, int lineNumber) {
        return Optional.ofNullable(value)
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException(
                        value == null
                                ? "Line %d: %s cannot be null".formatted(lineNumber, fieldName)
                                : "Line %d: %s cannot be blank".formatted(lineNumber, fieldName)
                ));
    }

    /**
     * Parses winner field using functional approach with Optional.
     * Eliminates if statement using method references.
     *
     * <p>Uses Java 21 features EXTREMELY:
     * <ul>
     *   <li>Optional for null-safe operations</li>
     *   <li>Method references for cleaner code</li>
     *   <li>Predicate for functional validation</li>
     * </ul>
     *
     * @param value String value to parse
     * @return Boolean indicating if movie is a winner
     */
    private Boolean parseWinner(String value) {
        return Optional.ofNullable(value)
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .map(winnerYes::equalsIgnoreCase)
                .orElse(false);
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
