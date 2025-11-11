package golden.raspberry.awards.infrastructure.adapter.driven.persistence.repository;

import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    /**
     * Finds movies by title containing the search term (case-insensitive).
     * @param searchTerm Search term
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieEntity
     */
    @Query("SELECT m FROM MovieEntity m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<MovieEntity> findByTitleContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Finds movies by year matching the search term.
     * @param searchTerm Search term (will be converted to integer)
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieEntity
     */
    @Query("SELECT m FROM MovieEntity m WHERE CAST(m.year AS string) LIKE CONCAT('%', :searchTerm, '%')")
    Page<MovieEntity> findByYearContaining(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Finds movies by studios containing the search term (case-insensitive).
     * @param searchTerm Search term
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieEntity
     */
    @Query("SELECT m FROM MovieEntity m WHERE LOWER(m.studios) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<MovieEntity> findByStudiosContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Finds movies by producers containing the search term (case-insensitive).
     * @param searchTerm Search term
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieEntity
     */
    @Query("SELECT m FROM MovieEntity m WHERE LOWER(m.producers) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<MovieEntity> findByProducersContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Finds movies by ID matching the search term.
     * @param searchTerm Search term (will be converted to long)
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieEntity
     */
    @Query("SELECT m FROM MovieEntity m WHERE CAST(m.id AS string) LIKE CONCAT('%', :searchTerm, '%')")
    Page<MovieEntity> findByIdContaining(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Finds movies by any field containing the search term (case-insensitive).
     * @param searchTerm Search term
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieEntity
     */
    @Query("SELECT m FROM MovieEntity m WHERE " +
           "LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.studios) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.producers) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(m.year AS string) LIKE CONCAT('%', :searchTerm, '%') OR " +
           "CAST(m.id AS string) LIKE CONCAT('%', :searchTerm, '%')")
    Page<MovieEntity> findByAllFieldsContaining(@Param("searchTerm") String searchTerm, Pageable pageable);
}

