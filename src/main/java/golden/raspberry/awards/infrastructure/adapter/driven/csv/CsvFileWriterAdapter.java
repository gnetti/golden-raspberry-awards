package golden.raspberry.awards.infrastructure.adapter.driven.csv;

import com.opencsv.CSVWriter;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
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
 * Implements CsvFileWriterPort and synchronizes database changes with CSV file.
 * <p>CSV format: id;year;title;studios;producers;winner;;
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class CsvFileWriterAdapter implements CsvFileWriterPort {

    private static final String CSV_HEADER = "id;year;title;studios;producers;winner;;";
    private static final int ID_COLUMN_INDEX = 0;

    private final String csvFile;
    private final char csvSeparator;
    private final String winnerYes;

    /**
     * Constructor for dependency injection.
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

    /**
     * Appends a new movie to the CSV file.
     * @param movie Movie with ID to append to CSV file
     * @throws NullPointerException if movie is null
     * @throws IllegalStateException if CSV file cannot be read or written
     */
    @Override
    public void appendMovie(MovieWithId movie) {
        Objects.requireNonNull(movie, "Movie cannot be null");

        var csvData = readCsvData();
        var newLine = convertMovieToCsvLine(movie);
        var updatedDataLines = Stream.concat(
                        csvData.dataLines().stream(),
                        Stream.<String[]>of(newLine))
                .toList();

        writeCsvData(new CsvData(csvData.headerLine(), updatedDataLines));
    }

    /**
     * Updates an existing movie in the CSV file.
     * @param movie Movie with ID to update in CSV file
     * @throws NullPointerException if movie is null
     * @throws IllegalStateException if movie is not found in CSV or file cannot be read/written
     */
    @Override
    public void updateMovie(MovieWithId movie) {
        Objects.requireNonNull(movie, "Movie cannot be null");

        var csvData = readCsvData();
        var movieId = String.valueOf(movie.id());

        var updatedDataLines = csvData.dataLines().stream()
                .map(line -> updateLineIfMatches(line, movieId, movie))
                .toList();

        validateMovieExists(updatedDataLines, movieId, csvData.dataLines());
        writeCsvData(new CsvData(csvData.headerLine(), updatedDataLines));
    }

    /**
     * Removes a movie from the CSV file by ID.
     * @param id Movie ID to remove from CSV file
     * @throws NullPointerException if id is null
     * @throws IllegalStateException if movie is not found in CSV or file cannot be read/written
     */
    @Override
    public void removeMovie(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        var csvData = readCsvData();
        var movieId = String.valueOf(id);

        var updatedDataLines = csvData.dataLines().stream()
                .filter(line -> !isLineForMovie(line, movieId))
                .toList();

        validateMovieExistsBeforeRemoval(updatedDataLines, movieId, csvData.dataLines());
        writeCsvData(new CsvData(csvData.headerLine(), updatedDataLines));
    }

    /**
     * Reads CSV data from file system or classpath.
     * Attempts to read from file system first, then falls back to classpath if not found.
     * If neither exists, returns default header line.
     *
     * @return CsvData containing header line and data lines
     * @throws IllegalStateException if CSV file cannot be read from file system or classpath
     */
    private CsvData readCsvData() {
        var fileSystemPath = Path.of("src/main/resources", csvFile);
        
        var allLines = Optional.<List<String[]>>empty()
                .or(() -> Optional.of(fileSystemPath)
                        .filter(Files::exists)
                        .map(this::readFromFileSystem))
                .or(this::readFromClasspath)
                .orElseGet(() -> List.<String[]>of(CSV_HEADER.split(String.valueOf(csvSeparator))));

        return separateHeaderAndData(allLines);
    }

    /**
     * Separates CSV header line from data lines.
     * Takes the first line as header and filters valid data lines from the rest.
     *
     * @param allLines All CSV lines including header
     * @return CsvData with separated header and valid data lines
     */
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
                .orElseGet(() -> new CsvData(CSV_HEADER.split(String.valueOf(csvSeparator)), List.of()));
    }

    /**
     * Validates if a CSV line is a valid data line.
     * A line is valid if it has at least one column and the ID column is not empty.
     *
     * @param line CSV line as string array
     * @return true if line is valid, false otherwise
     */
    private boolean isValidDataLine(String[] line) {
        return Optional.ofNullable(line)
                .filter(l -> l.length > ID_COLUMN_INDEX)
                .map(l -> Optional.ofNullable(l[ID_COLUMN_INDEX])
                        .map(id -> !id.trim().isEmpty())
                        .orElse(false))
                .orElse(false);
    }

    /**
     * Reads CSV data from file system.
     * Uses OpenCSV library to parse the CSV file with the configured separator.
     *
     * @param path File system path to CSV file
     * @return List of CSV lines as string arrays, or default header if file is empty
     * @throws IllegalStateException if CSV file cannot be read or parsed
     */
    private List<String[]> readFromFileSystem(Path path) {
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             var csvReader = new com.opencsv.CSVReaderBuilder(reader)
                     .withCSVParser(new com.opencsv.CSVParserBuilder()
                             .withSeparator(csvSeparator)
                             .build())
                     .build()) {

            return Optional.ofNullable(csvReader.readAll())
                    .filter(lines -> !lines.isEmpty())
                    .orElseGet(() -> List.<String[]>of(CSV_HEADER.split(String.valueOf(csvSeparator))));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read CSV file: %s".formatted(csvFile), e);
        }
    }

    /**
     * Attempts to read CSV data from classpath.
     * Returns Optional.empty() if resource does not exist.
     *
     * @return Optional containing list of CSV lines if resource exists, empty otherwise
     */
    private Optional<List<String[]>> readFromClasspath() {
        return Optional.of(new ClassPathResource(csvFile))
                .filter(ClassPathResource::exists)
                .map(this::readFromClasspathResource);
    }

    /**
     * Reads CSV data from classpath resource.
     * Uses OpenCSV library to parse the CSV file with the configured separator.
     *
     * @param resource ClassPathResource pointing to CSV file
     * @return List of CSV lines as string arrays, or default header if file is empty
     * @throws IllegalStateException if CSV file cannot be read or parsed
     */
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
                    .orElseGet(() -> List.<String[]>of(CSV_HEADER.split(String.valueOf(csvSeparator))));
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read CSV file from classpath: %s".formatted(csvFile), e);
        }
    }

    /**
     * Writes CSV data to file system.
     * Creates parent directories if they don't exist and writes header and data lines.
     *
     * @param csvData CsvData containing header line and data lines to write
     * @throws IllegalStateException if CSV file cannot be written or directories cannot be created
     */
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
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to write CSV file: %s".formatted(csvFile), e);
        }
    }

    /**
     * Converts a MovieWithId domain object to CSV line format.
     * Creates a string array with all movie fields in the correct order.
     * <p>CSV format: id;year;title;studios;producers;winner;;
     *
     * @param movie Movie with ID to convert
     * @return String array representing CSV line
     */
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

    /**
     * Updates a CSV line if it matches the given movie ID.
     * Returns the updated line if match is found, otherwise returns the original line unchanged.
     *
     * @param line    CSV line to check and potentially update
     * @param movieId Movie ID to match against
     * @param movie   Movie with ID to use for update
     * @return Updated CSV line if match found, original line otherwise
     */
    private String[] updateLineIfMatches(String[] line, String movieId, MovieWithId movie) {
        return Optional.ofNullable(line)
                .filter(l -> l.length > ID_COLUMN_INDEX)
                .filter(l -> movieId.equals(l[ID_COLUMN_INDEX]))
                .map(l -> convertMovieToCsvLine(movie))
                .orElse(line);
    }

    /**
     * Checks if a CSV line belongs to a specific movie by ID.
     * Validates that the line has enough columns and the ID column matches.
     *
     * @param line    CSV line to check
     * @param movieId Movie ID to match against
     * @return true if line belongs to the movie, false otherwise
     */
    private boolean isLineForMovie(String[] line, String movieId) {
        return Optional.ofNullable(line)
                .filter(l -> l.length > ID_COLUMN_INDEX)
                .map(l -> movieId.equals(l[ID_COLUMN_INDEX]))
                .orElse(false);
    }

    /**
     * Validates that a movie exists in the original CSV before update.
     * Also validates that the movie still exists after update operation.
     *
     * @param updatedLines  CSV lines after update operation
     * @param movieId       Movie ID to validate
     * @param originalLines Original CSV lines before update
     * @throws IllegalStateException if movie is not found in original CSV or not present after update
     */
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

    /**
     * Validates that a movie exists in the original CSV before removal.
     * Also validates that the movie no longer exists after removal operation.
     *
     * @param updatedLines  CSV lines after removal operation
     * @param movieId       Movie ID to validate
     * @param originalLines Original CSV lines before removal
     * @throws IllegalStateException if movie is not found in original CSV or still exists after removal
     */
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
     * Internal record for separating CSV header from data lines.
     * Internal record for separating CSV header from data lines.
     */
    private record CsvData(String[] headerLine, List<String[]> dataLines) {
        CsvData {
            Objects.requireNonNull(headerLine, "Header line cannot be null");
            Objects.requireNonNull(dataLines, "Data lines cannot be null");
        }
    }
}

