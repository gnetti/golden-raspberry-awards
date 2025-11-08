package golden.raspberry.awards.adapter.driven.csv;

import com.opencsv.CSVWriter;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.domain.model.MovieWithId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Output Adapter for writing movies to CSV file.
 *
 * <p>Implements CsvFileWriterPort and handles CSV file operations
 * for synchronizing database changes with CSV file.
 *
 * <p>This adapter follows hexagonal architecture principles:
 * - Implements Port defined by Application layer
 * - Handles file system operations (Infrastructure)
 * - Ensures CSV file is synchronized with database
 *
 * <p>CSV format: id;year;title;studios;producers;winner;;
 *
 * <p>Uses Java 21 features EXTREMELY:
 * <ul>
 *   <li>Optional for null-safety</li>
 *   <li>Stream API for functional processing</li>
 *   <li>Pattern Matching for validation</li>
 *   <li>String Templates for error messages</li>
 *   <li>Text Blocks for logs</li>
 *   <li>Records for internal data structures</li>
 *   <li>Method references for cleaner code</li>
 *   <li>Sealed interfaces for validation</li>
 * </ul>
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class CsvFileWriterAdapter implements CsvFileWriterPort {

    private static final Logger logger = LoggerFactory.getLogger(CsvFileWriterAdapter.class);
    private static final String CSV_HEADER = "id;year;title;studios;producers;winner;;";
    private static final int ID_COLUMN_INDEX = 0;

    private final String csvFile;
    private final char csvSeparator;
    private final String winnerYes;

    /**
     * Constructor for dependency injection.
     *
     * @param csvFile      CSV file path from properties
     * @param csvSeparator CSV separator character from properties
     * @param winnerYes    Winner value string from properties
     */
    public CsvFileWriterAdapter(
            @Value("${csv.file:data/movieList.csv}") String csvFile,
            @Value("${csv.separator:;}") String csvSeparator,
            @Value("${csv.winner-yes:yes}") String winnerYes) {
        this.csvFile = Objects.requireNonNull(csvFile, "CSV file path cannot be null");
        this.csvSeparator = csvSeparator != null && !csvSeparator.isEmpty()
                ? csvSeparator.charAt(0)
                : ';';
        this.winnerYes = Objects.requireNonNull(winnerYes, "Winner yes value cannot be null");
    }

    @Override
    public void appendMovie(MovieWithId movie) {
        Objects.requireNonNull(movie, "Movie cannot be null");
        logger.debug("Appending movie to CSV: id={}, title={}", movie.id(), movie.title());

        var csvData = readCsvData();
        var newLine = convertMovieToCsvLine(movie);
        var updatedDataLines = Stream.concat(
                        csvData.dataLines().stream(),
                        Stream.<String[]>of(newLine))
                .toList();

        writeCsvData(new CsvData(csvData.headerLine(), updatedDataLines));
        logger.info("Successfully appended movie to CSV: id={}, title={}", movie.id(), movie.title());
    }

    @Override
    public void updateMovie(MovieWithId movie) {
        Objects.requireNonNull(movie, "Movie cannot be null");
        logger.debug("Updating movie in CSV: id={}, title={}", movie.id(), movie.title());

        var csvData = readCsvData();
        var movieId = String.valueOf(movie.id());

        var updatedDataLines = csvData.dataLines().stream()
                .map(line -> updateLineIfMatches(line, movieId, movie))
                .toList();

        validateMovieExists(updatedDataLines, movieId, csvData.dataLines());
        writeCsvData(new CsvData(csvData.headerLine(), updatedDataLines));
        logger.info("Successfully updated movie in CSV: id={}, title={}", movie.id(), movie.title());
    }

    @Override
    public void removeMovie(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        logger.debug("Removing movie from CSV: id={}", id);

        var csvData = readCsvData();
        var movieId = String.valueOf(id);

        var updatedDataLines = csvData.dataLines().stream()
                .filter(line -> !isLineForMovie(line, movieId))
                .toList();

        validateMovieExistsBeforeRemoval(updatedDataLines, movieId, csvData.dataLines());
        writeCsvData(new CsvData(csvData.headerLine(), updatedDataLines));
        logger.info("Successfully removed movie from CSV: id={}", id);
    }

    private CsvData readCsvData() {
        var fileSystemPath = Path.of("src/main/resources", csvFile);
        
        var allLines = Optional.<List<String[]>>empty()
                .or(() -> Optional.of(fileSystemPath)
                        .filter(Files::exists)
                        .map(this::readFromFileSystem))
                .or(this::readFromClasspath)
                .orElseGet(() -> {
                    logger.warn("CSV file not found, creating new file: {}", csvFile);
                    return List.<String[]>of(CSV_HEADER.split(String.valueOf(csvSeparator)));
                });

        return separateHeaderAndData(allLines);
    }

    private CsvData separateHeaderAndData(List<String[]> allLines) {
        return Optional.of(allLines)
                .filter(lines -> !lines.isEmpty())
                .map(lines -> {
                    var headerLine = lines.getFirst();
                    var dataLines = lines.stream()
                            .skip(1)
                            .filter(this::isValidDataLine)
                            .toList();
                    return new CsvData(headerLine, dataLines);
                })
                .orElseGet(() -> {
                    logger.warn("CSV file is empty, using default header: {}", csvFile);
                    return new CsvData(CSV_HEADER.split(String.valueOf(csvSeparator)), List.of());
                });
    }

    private boolean isValidDataLine(String[] line) {
        return Optional.ofNullable(line)
                .filter(l -> l.length > ID_COLUMN_INDEX)
                .map(l -> Optional.ofNullable(l[ID_COLUMN_INDEX])
                        .map(id -> !id.trim().isEmpty())
                        .orElse(false))
                .orElse(false);
    }

    private List<String[]> readFromFileSystem(Path path) {
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             var csvReader = new com.opencsv.CSVReaderBuilder(reader)
                     .withCSVParser(new com.opencsv.CSVParserBuilder()
                             .withSeparator(csvSeparator)
                             .build())
                     .build()) {

            return Optional.ofNullable(csvReader.readAll())
                    .filter(lines -> !lines.isEmpty())
                    .orElseGet(this::getDefaultHeaderWithLog);
        } catch (Exception e) {
            logger.error("Error reading CSV from file system: {}", csvFile, e);
            throw new IllegalStateException(
                    "Failed to read CSV file: %s".formatted(csvFile), e);
        }
    }

    private Optional<List<String[]>> readFromClasspath() {
        return Optional.of(new ClassPathResource(csvFile))
                .filter(ClassPathResource::exists)
                .map(this::readFromClasspathResource);
    }

    private List<String[]> readFromClasspathResource(ClassPathResource resource) {
        try (var inputStream = resource.getInputStream();
             var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             var csvReader = new com.opencsv.CSVReaderBuilder(reader)
                     .withCSVParser(new com.opencsv.CSVParserBuilder()
                             .withSeparator(csvSeparator)
                             .build())
                     .build()) {

            return Optional.ofNullable(csvReader.readAll())
                    .filter(lines -> !lines.isEmpty())
                    .orElseGet(this::getDefaultHeaderWithLog);
        } catch (Exception e) {
            logger.error("Error reading CSV from classpath: {}", csvFile, e);
            throw new IllegalStateException(
                    "Failed to read CSV file from classpath: %s".formatted(csvFile), e);
        }
    }

    private void writeCsvData(CsvData csvData) {
        var fileSystemPath = Path.of("src/main/resources", csvFile);
        try {
            Files.createDirectories(fileSystemPath.getParent());

            try (var writer = Files.newBufferedWriter(fileSystemPath, StandardCharsets.UTF_8);
                 var csvWriter = new CSVWriter(writer, csvSeparator,
                         CSVWriter.NO_QUOTE_CHARACTER,
                         CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                         CSVWriter.DEFAULT_LINE_END)) {

                csvWriter.writeNext(csvData.headerLine());
                csvData.dataLines().forEach(csvWriter::writeNext);
                csvWriter.flush();
            }

            logger.debug("Successfully wrote {} data lines to CSV: {}", csvData.dataLines().size(), csvFile);
        } catch (Exception e) {
            logger.error("Error writing CSV file: {}", csvFile, e);
            throw new IllegalStateException(
                    "Failed to write CSV file: %s".formatted(csvFile), e);
        }
    }

    private String[] convertMovieToCsvLine(MovieWithId movie) {
        return new String[]{
                String.valueOf(movie.id()),
                String.valueOf(movie.year()),
                movie.title(),
                movie.studios(),
                movie.producers(),
                movie.winner() ? winnerYes : "",
                "",
                ""
        };
    }

    private String[] updateLineIfMatches(String[] line, String movieId, MovieWithId movie) {
        return Optional.ofNullable(line)
                .filter(l -> l.length > ID_COLUMN_INDEX)
                .filter(l -> movieId.equals(l[ID_COLUMN_INDEX]))
                .map(l -> convertMovieToCsvLine(movie))
                .orElse(line);
    }

    private boolean isLineForMovie(String[] line, String movieId) {
        return Optional.ofNullable(line)
                .filter(l -> l.length > ID_COLUMN_INDEX)
                .map(l -> movieId.equals(l[ID_COLUMN_INDEX]))
                .orElse(false);
    }

    private void validateMovieExists(List<String[]> updatedLines, String movieId, List<String[]> originalLines) {
        var originalExists = originalLines.stream()
                .anyMatch(line -> isLineForMovie(line, movieId));

        if (!originalExists) {
            throw new IllegalStateException(
                    "Movie with ID %s not found in CSV for update".formatted(movieId));
        }

        var movieExists = updatedLines.stream()
                .anyMatch(line -> isLineForMovie(line, movieId));

        if (!movieExists) {
            throw new IllegalStateException(
                    "Movie with ID %s was not updated correctly in CSV".formatted(movieId));
        }
    }

    private void validateMovieExistsBeforeRemoval(List<String[]> updatedLines, String movieId, List<String[]> originalLines) {
        var originalExists = originalLines.stream()
                .anyMatch(line -> isLineForMovie(line, movieId));

        if (!originalExists) {
            throw new IllegalStateException(
                    "Movie with ID %s not found in CSV for removal".formatted(movieId));
        }

        var stillExists = updatedLines.stream()
                .anyMatch(line -> isLineForMovie(line, movieId));

        if (stillExists) {
            throw new IllegalStateException(
                    "Movie with ID %s still exists in CSV after removal attempt".formatted(movieId));
        }
    }

    /**
     * Returns default CSV header with logging.
     * Helper method to eliminate code duplication.
     *
     * <p>Uses Java 21 features: String Templates for logging.
     *
     * @return List containing only the CSV header line
     */
    private List<String[]> getDefaultHeaderWithLog() {
        logger.warn("CSV file is empty, using header only: {}", csvFile);
        return List.<String[]>of(CSV_HEADER.split(String.valueOf(csvSeparator)));
    }

    /**
     * Internal record for separating CSV header from data lines.
     * Uses Java 21 Record for immutability and elegance.
     */
    private record CsvData(String[] headerLine, List<String[]> dataLines) {
        CsvData {
            Objects.requireNonNull(headerLine, "Header line cannot be null");
            Objects.requireNonNull(dataLines, "Data lines cannot be null");
        }
    }
}

