package golden.raspberry.awards.adapter.driven.persistence.adapter;

import golden.raspberry.awards.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.adapter.driven.persistence.mapper.MovieMapper;
import golden.raspberry.awards.adapter.driven.persistence.mapper.MovieWithIdMapper;
import golden.raspberry.awards.adapter.driven.persistence.repository.MovieJpaRepository;
import golden.raspberry.awards.core.application.port.out.GetMovieWithIdPort;
import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.MovieWithId;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adapter implementing MovieRepositoryPort and GetMovieWithIdPort.
 * Converts between Domain Model and JPA Entity.
 * <p>
 * Follows Hexagonal Architecture: adapter implements ports.
 * Uses Spring for dependency injection.
 */
@Component
public class MovieRepositoryAdapter implements MovieRepositoryPort, GetMovieWithIdPort {

    private final MovieJpaRepository jpaRepository;

    public MovieRepositoryAdapter(MovieJpaRepository jpaRepository) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "JpaRepository cannot be null");
    }

    @Override
    public List<Movie> findByWinnerTrue() {
        var entities = jpaRepository.findByWinnerTrue();
        return MovieMapper.toDomainList(entities);
    }

    @Override
    public void saveAll(List<Movie> movies) {
        Objects.requireNonNull(movies, "Movies list cannot be null");
        var entities = MovieMapper.toEntityList(movies);
        jpaRepository.saveAll(entities);
    }

    @Override
    public Movie save(Movie movie) {
        Objects.requireNonNull(movie, "Movie cannot be null");
        var entity = MovieMapper.toEntity(movie);
        var savedEntity = jpaRepository.save(entity);
        return MovieMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<MovieWithId> findByIdWithId(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return jpaRepository.findById(id)
                .flatMap(MovieWithIdMapper::toDomain);
    }

    @Override
    public Optional<MovieWithId> findByYearAndTitle(Integer year, String title) {
        Objects.requireNonNull(year, "Year cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getYear().equals(year) && entity.getTitle().equals(title))
                .findFirst()
                .flatMap(MovieWithIdMapper::toDomain);
    }

    @Override
    public Optional<Movie> findById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return jpaRepository.findById(id)
                .map(MovieMapper::toDomain);
    }

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

