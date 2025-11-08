package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.MovieWithId;

/**
 * Output Port for writing movies to CSV file.
 * Defined by Application, implemented by Adapters.
 * Pure interface - no Spring dependencies.
 *
 * <p>This port defines the contract for external services that write
 * movie data to CSV files, ensuring synchronization between database
 * and CSV file.
 *
 * <p>Uses Java 21 features: Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface CsvFileWriterPort {

    /**
     * Appends a new movie to the CSV file.
     * The movie is added at the end of the file, maintaining the CSV format.
     *
     * @param movie The movie with ID to append to CSV
     * @throws NullPointerException  if movie is null
     * @throws IllegalStateException if the CSV file cannot be written
     */
    void appendMovie(MovieWithId movie);

    /**
     * Updates an existing movie in the CSV file.
     * Finds the movie by ID and updates its data.
     *
     * @param movie The movie with ID to update in CSV
     * @throws NullPointerException  if movie is null
     * @throws IllegalStateException if the movie is not found or CSV cannot be written
     */
    void updateMovie(MovieWithId movie);

    /**
     * Removes a movie from the CSV file by ID.
     * Finds the movie by ID and removes it from the CSV.
     *
     * @param id The ID of the movie to remove
     * @throws NullPointerException  if id is null
     * @throws IllegalStateException if the movie is not found or CSV cannot be written
     */
    void removeMovie(Long id);
}

