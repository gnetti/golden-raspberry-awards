package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

/**
 * Output Port for writing movies to CSV file.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface CsvFileWriterPort {

    /**
     * Appends a new movie to the CSV file.
     *
     * @param movie The movie with ID to append to CSV
     * @throws NullPointerException  if movie is null
     * @throws IllegalStateException if the CSV file cannot be written
     */
    void appendMovie(MovieWithId movie);

    /**
     * Updates an existing movie in the CSV file.
     *
     * @param movie The movie with ID to update in CSV
     * @throws NullPointerException  if movie is null
     * @throws IllegalStateException if the movie is not found or CSV cannot be written
     */
    void updateMovie(MovieWithId movie);

    /**
     * Removes a movie from the CSV file by ID.
     *
     * @param id The ID of the movie to remove
     * @throws NullPointerException  if id is null
     * @throws IllegalStateException if the movie is not found or CSV cannot be written
     */
    void removeMovie(Long id);
}

