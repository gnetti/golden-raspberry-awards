package golden.raspberry.awards.adapter.driven.persistence.adapter;

import golden.raspberry.awards.adapter.driven.persistence.mapper.MovieMapper;
import golden.raspberry.awards.adapter.driven.persistence.repository.MovieJpaRepository;
import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Adapter implementing MovieRepositoryPort.
 * Converts between Domain Model and JPA Entity.
 * <p>
 * Follows Hexagonal Architecture: adapter implements port.
 * Uses Spring for dependency injection.
 */
@Component
public class MovieRepositoryAdapter implements MovieRepositoryPort {

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
}

