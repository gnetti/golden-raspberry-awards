package golden.raspberry.awards.adapter.driven.persistence.repository;

import golden.raspberry.awards.adapter.driven.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for MovieEntity.
 * Provides database operations for movies.
 */
@Repository
public interface MovieJpaRepository extends JpaRepository<MovieEntity, Long> {
    
    /**
     * Finds all movies that are winners.
     * 
     * @return List of winning MovieEntity objects
     */
    List<MovieEntity> findByWinnerTrue();
}

