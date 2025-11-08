package golden.raspberry.awards.adapter.driving.rest;

import golden.raspberry.awards.adapter.driving.rest.dto.CreateMovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.UpdateMovieDTO;
import golden.raspberry.awards.adapter.driving.rest.mapper.MovieDTOMapper;
import golden.raspberry.awards.core.application.port.in.CreateMovieUseCase;
import golden.raspberry.awards.core.application.port.in.DeleteMovieUseCase;
import golden.raspberry.awards.core.application.port.in.GetMovieUseCase;
import golden.raspberry.awards.core.application.port.in.UpdateMovieUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * REST Controller for Movie CRUD operations.
 *
 * <p>Implements Richardson Maturity Level 2:
 * <ul>
 *   <li><strong>Resources:</strong> /api/movies, /api/movies/{id}</li>
 *   <li><strong>HTTP Verbs:</strong> GET, POST, PUT, DELETE</li>
 *   <li><strong>Status Codes:</strong> 200 OK, 201 Created, 400 Bad Request, 404 Not Found, 500 Internal Server Error</li>
 *   <li><strong>Structured Error Messages:</strong> via ApiExceptionHandler</li>
 * </ul>
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Input Adapter (Primary) - receives HTTP requests</li>
 *   <li>Calls Use Cases (Application layer)</li>
 *   <li>Converts Domain models to DTOs</li>
 *   <li>Returns JSON responses</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, var, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final CreateMovieUseCase createMovieUseCase;
    private final GetMovieUseCase getMovieUseCase;
    private final UpdateMovieUseCase updateMovieUseCase;
    private final DeleteMovieUseCase deleteMovieUseCase;

    /**
     * Constructor for dependency injection.
     *
     * @param createMovieUseCase Use case for creating movies
     * @param getMovieUseCase    Use case for getting movies
     * @param updateMovieUseCase Use case for updating movies
     * @param deleteMovieUseCase Use case for deleting movies
     */
    public MovieController(
            CreateMovieUseCase createMovieUseCase,
            GetMovieUseCase getMovieUseCase,
            UpdateMovieUseCase updateMovieUseCase,
            DeleteMovieUseCase deleteMovieUseCase) {

        this.createMovieUseCase = Objects.requireNonNull(createMovieUseCase, "CreateMovieUseCase cannot be null");
        this.getMovieUseCase = Objects.requireNonNull(getMovieUseCase, "GetMovieUseCase cannot be null");
        this.updateMovieUseCase = Objects.requireNonNull(updateMovieUseCase, "UpdateMovieUseCase cannot be null");
        this.deleteMovieUseCase = Objects.requireNonNull(deleteMovieUseCase, "DeleteMovieUseCase cannot be null");
    }

    /**
     * Creates a new movie.
     *
     * <p>Endpoint: POST /api/movies
     *
     * <p>Request body:
     * <pre>
     * {
     *   "year": 2024,
     *   "title": "Movie Title",
     *   "studios": "Studio Name",
     *   "producers": "Producer Name",
     *   "winner": false
     * }
     * </pre>
     *
     * @param createDTO Request DTO with movie data (validated with @Valid and Jakarta Validation)
     * @return ResponseEntity with created MovieDTO (201 Created)
     * @apiNote Status Code: 201 Created (success) or 400 Bad Request (via exception handler)
     */
    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@RequestBody @Valid CreateMovieDTO createDTO) {
        Objects.requireNonNull(createDTO, "Request body cannot be null");
        var movieWithId = createMovieUseCase.execute(
                createDTO.year(),
                createDTO.title(),
                createDTO.studios(),
                createDTO.producers(),
                createDTO.winner()
        );

        var movieDTO = MovieDTOMapper.toDTO(movieWithId)
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to convert MovieWithId to MovieDTO"
                ));

        return ResponseEntity.status(HttpStatus.CREATED).body(movieDTO);
    }

    /**
     * Gets a movie by ID.
     *
     * <p>Endpoint: GET /api/movies/{id}
     *
     * @param id Movie ID (path variable, must be >= 1)
     * @return ResponseEntity with MovieDTO (200 OK)
     * @apiNote Status Code: 200 OK (success) or 400 Bad Request (invalid ID), 404 Not Found (via exception handler)
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovie(@PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id) {
        var movieWithId = getMovieUseCase.execute(id);
        var movieDTO = MovieDTOMapper.toDTO(movieWithId)
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to convert MovieWithId to MovieDTO"
                ));
        return ResponseEntity.ok(movieDTO);
    }

    /**
     * Updates an existing movie.
     *
     * <p>Endpoint: PUT /api/movies/{id}
     *
     * <p>Request body:
     * <pre>
     * {
     *   "year": 2024,
     *   "title": "Updated Movie Title",
     *   "studios": "Updated Studio Name",
     *   "producers": "Updated Producer Name",
     *   "winner": true
     * }
     * </pre>
     *
     * @param id Movie ID (path variable, must be >= 1)
     * @param updateDTO Request DTO with updated movie data (validated with @Valid and Jakarta Validation)
     * @return ResponseEntity with updated MovieDTO (200 OK)
     * @apiNote Status Code: 200 OK (success) or 400 Bad Request, 404 Not Found (via exception handler)
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(
            @PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id,
            @RequestBody @Valid UpdateMovieDTO updateDTO) {
        Objects.requireNonNull(updateDTO, "Request body cannot be null");
        var movieWithId = updateMovieUseCase.execute(
                id,
                updateDTO.year(),
                updateDTO.title(),
                updateDTO.studios(),
                updateDTO.producers(),
                updateDTO.winner()
        );

        var movieDTO = MovieDTOMapper.toDTO(movieWithId)
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to convert MovieWithId to MovieDTO"
                ));

        return ResponseEntity.ok(movieDTO);
    }

    /**
     * Deletes a movie by ID.
     *
     * <p>Endpoint: DELETE /api/movies/{id}
     *
     * @param id Movie ID (path variable, must be >= 1)
     * @return ResponseEntity with no content (204 No Content)
     * @apiNote Status Code: 204 No Content (success) or 400 Bad Request (invalid ID), 404 Not Found (via exception handler)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id) {
        deleteMovieUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

