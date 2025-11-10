package golden.raspberry.awards.infrastructure.adapter.driven.persistence.repository;

import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for MovieEntity.
 * Provides database operations for movies.
 *
 *  @author Luis Generoso
 *  * @since 1.0.0
 */
@Repository
public interface MovieJpaRepository extends JpaRepository<MovieEntity, Long> {
    
    /**
     * Finds all movies that are winners.
     * @return List of winning MovieEntity objects
     */
    List<MovieEntity> findByWinnerTrue();

    /**
     * Gets the maximum ID from the database.
     * Returns empty if no movies exist.
     * @return Optional containing the maximum ID, or empty if no movies exist
     */
    @Query("SELECT MAX(m.id) FROM MovieEntity m")
    Optional<Long> findMaxId();
}

