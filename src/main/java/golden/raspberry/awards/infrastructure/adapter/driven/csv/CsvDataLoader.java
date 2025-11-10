package golden.raspberry.awards.infrastructure.adapter.driven.csv;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.repository.MovieJpaRepository;
import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
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
 * Implements CommandLineRunner to execute CSV loading automatically when the application starts.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class CsvDataLoader implements CommandLineRunner {

    private final MovieJpaRepository jpaRepository;
    private final IdKeyManagerPort idKeyManagerPort;
    private final boolean resetToOriginal;
    private final String csvFile;
    private final String csvOriginalFile;
    private final char csvSeparator;
    private final int minColumns;
    private final String winnerYes;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository    JPA repository for saving movies with IDs from CSV
     * @param idKeyManagerPort Port for managing ID keys in XML
     * @param resetToOriginal  If true, resets CSV to original file from primary-base on startup
     * @param csvFile          CSV file path from properties
     * @param csvOriginalFile  Original CSV file path from properties
     * @param csvSeparator     CSV separator character from properties
     * @param minColumns       Minimum required columns from properties (6: id;year;title;studios;producers;winner)
     * @param winnerYes        Winner value string from properties
     */
    public CsvDataLoader(
            MovieJpaRepository jpaRepository,
            IdKeyManagerPort idKeyManagerPort,
            @Value("${csv.reset-to-original:false}") boolean resetToOriginal,
            @Value("${csv.file:data/movieList.csv}") String csvFile,
            @Value("${csv.original-file:data/primary-base/MovieList.csv}") String csvOriginalFile,
            @Value("${csv.separator:;}") String csvSeparator,
            @Value("${csv.min-columns:6}") int minColumns,
            @Value("${csv.winner-yes:yes}") String winnerYes) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "JpaRepository cannot be null");
        this.idKeyManagerPort = Objects.requireNonNull(idKeyManagerPort, "IdKeyManagerPort cannot be null");
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
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        try {
            if (resetToOriginal) {
                resetCsvToOriginal();
            }

            var entities = loadMoviesFromCsv();
            saveMoviesWithIds(entities);

            var maxIdFromDatabase = jpaRepository.findMaxId().orElse(0L);
            Optional.of(resetToOriginal)
                    .filter(Boolean::booleanValue)
                    .ifPresentOrElse(
                            ignored -> idKeyManagerPort.resetLastId(maxIdFromDatabase),
                            () -> idKeyManagerPort.synchronizeWithDatabase(maxIdFromDatabase)
                    );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load CSV data: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * Resets the CSV file to the original file from primary-base.
     *
     * @throws Exception if original CSV file cannot be read or target file cannot be written
     */
    private void resetCsvToOriginal() throws Exception {
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
    }

    /**
     * Saves movies with IDs from CSV directly to database.
     * Uses JPA repository to preserve IDs from CSV.
     * @param entities List of MovieEntity with IDs from CSV
     */
    private void saveMoviesWithIds(List<MovieEntity> entities) {
        jpaRepository.saveAll(entities);
    }

    /**
     * Loads movies from CSV file.
     *
     * @return List of MovieEntity with IDs from CSV
     * @throws Exception if CSV file cannot be read or parsed
     */
    private List<MovieEntity> loadMoviesFromCsv() throws Exception {
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
                    .orElse(List.of());
        }
    }

    /**
     * Parses all CSV lines using functional approach.
     * Skips header (first line) and processes data lines.
     * Creates MovieEntity with IDs from CSV.
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
     *
     * @param line       CSV line as string array
     * @param lineNumber Line number for error reporting (1-based)
     * @return Optional containing MovieEntity with ID, or empty if parsing fails
     */
    private Optional<MovieEntity> parseEntityLineSafely(
            String[] line, int lineNumber) {
        return validateLine(line, lineNumber)
                .flatMap(valid -> parseMovieEntityLineSafely(valid.line(), valid.lineNumber()));
    }

    /**
     * Parses a validated CSV line safely into MovieEntity, returning Optional.empty() on error.
     * @param line       CSV line as string array (guaranteed valid)
     * @param lineNumber Line number for error reporting
     * @return Optional containing MovieEntity with ID, or empty if parsing fails
     */
    private Optional<MovieEntity> parseMovieEntityLineSafely(
            String[] line, int lineNumber) {
        try {
            return Optional.of(parseMovieEntityLine(line, lineNumber));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Validates a CSV line.
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
     *
     * @return Predicate that checks column count
     */
    private Predicate<String[]> hasMinimumColumns() {
        return line -> line.length >= minColumns;
    }

    /**
     * Parses a validated CSV line into a MovieEntity with ID from CSV.
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
     * Parses ID field.
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
     * Parses year field.
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
     * Validates year range.
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
     * Parses a string field.
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
     * Parses winner field.
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
     * Sealed interface for validation results.
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
    }
}

