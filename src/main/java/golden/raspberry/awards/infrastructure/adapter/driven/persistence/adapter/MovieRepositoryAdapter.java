package golden.raspberry.awards.infrastructure.adapter.driven.persistence.adapter;

import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.mapper.MovieMapper;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.mapper.MovieWithIdMapper;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.repository.MovieJpaRepository;
import golden.raspberry.awards.core.application.port.out.MovieQueryPort;
import golden.raspberry.awards.core.application.port.out.SaveMovieWithIdPort;
import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import golden.raspberry.awards.core.application.port.out.MovieRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Output Adapter for movie persistence operations.
 * Implements MovieRepositoryPort, SaveMovieWithIdPort, and MovieQueryPort.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class MovieRepositoryAdapter implements MovieRepositoryPort, SaveMovieWithIdPort, MovieQueryPort {

    private final MovieJpaRepository jpaRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param jpaRepository JPA repository for movie entities
     */
    public MovieRepositoryAdapter(MovieJpaRepository jpaRepository) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "JpaRepository cannot be null");
    }

    /**
     * Finds all movies where winner is true.
     *
     * @return List of winning movies
     */
    @Override
    public List<Movie> findByWinnerTrue() {
        var entities = jpaRepository.findByWinnerTrue();
        return MovieMapper.toDomainList(entities);
    }

    /**
     * Saves a movie with a specific ID to the repository.
     *
     * @param movie Movie to save
     * @param id ID to assign to the movie
     * @return Saved movie with ID
     * @throws IllegalStateException if movie cannot be converted to MovieWithId after saving
     */
    @Override
    public MovieWithId saveWithId(Movie movie, Long id) {
        Objects.requireNonNull(movie, "Movie cannot be null");
        Objects.requireNonNull(id, "ID cannot be null");
        var entity = new MovieEntity(
                id,
                movie.year(),
                movie.title(),
                movie.studios(),
                movie.producers(),
                movie.winner()
        );
        var savedEntity = jpaRepository.save(entity);
        return MovieWithIdMapper.toDomain(savedEntity)
                .orElseThrow(() -> new IllegalStateException(
                        "Movie was saved but could not be converted to MovieWithId: id=%d".formatted(id)
                ));
    }

    /**
     * Finds a movie with ID by its ID.
     *
     * @param id Movie ID
     * @return Optional containing MovieWithId if found, empty otherwise
     */
    @Override
    public Optional<MovieWithId> findByIdWithId(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return jpaRepository.findById(id)
                .flatMap(MovieWithIdMapper::toDomain);
    }

    /**
     * Finds a movie by its ID.
     *
     * @param id Movie ID
     * @return Optional containing Movie if found, empty otherwise
     */
    @Override
    public Optional<Movie> findById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return jpaRepository.findById(id)
                .map(MovieMapper::toDomain);
    }

    /**
     * Deletes a movie by its ID.
     *
     * @param id Movie ID
     * @return true if movie was deleted, false if movie doesn't exist
     */
    @Override
    public boolean deleteById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        if (!jpaRepository.existsById(id)) {
            return false;
        }
        jpaRepository.deleteById(id);
        return true;
    }

}

