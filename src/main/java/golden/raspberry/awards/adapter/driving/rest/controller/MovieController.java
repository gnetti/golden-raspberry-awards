package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.CreateMovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.UpdateMovieDTO;
import golden.raspberry.awards.core.application.port.in.CreateMoviePort;
import golden.raspberry.awards.core.application.port.in.DeleteMoviePort;
import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.in.UpdateMoviePort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * REST Controller for Movie CRUD operations.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {

    private final CreateMoviePort createMoviePort;
    private final GetMoviePort getMoviePort;
    private final UpdateMoviePort updateMoviePort;
    private final DeleteMoviePort deleteMoviePort;
    private final ConverterDtoPort converterDtoPort;

    /**
     * Constructor for dependency injection.
     *
     * @param createMoviePort Use case for creating movies
     * @param getMoviePort    Use case for getting movies
     * @param updateMoviePort Use case for updating movies
     * @param deleteMoviePort Use case for deleting movies
     * @param converterDtoPort Port for converting domain models to DTOs
     */
    public MovieController(
            CreateMoviePort createMoviePort,
            GetMoviePort getMoviePort,
            UpdateMoviePort updateMoviePort,
            DeleteMoviePort deleteMoviePort,
            ConverterDtoPort converterDtoPort) {

        this.createMoviePort = Objects.requireNonNull(createMoviePort, "CreateMoviePort cannot be null");
        this.getMoviePort = Objects.requireNonNull(getMoviePort, "GetMoviePort cannot be null");
        this.updateMoviePort = Objects.requireNonNull(updateMoviePort, "UpdateMoviePort cannot be null");
        this.deleteMoviePort = Objects.requireNonNull(deleteMoviePort, "DeleteMoviePort cannot be null");
        this.converterDtoPort = Objects.requireNonNull(converterDtoPort, "ConverterDtoPort cannot be null");
    }

    /**
     * Creates a new movie.
     *
     * @param createDTO Request DTO with movie data
     * @return ResponseEntity with created MovieDTO
     */
    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@RequestBody @Valid CreateMovieDTO createDTO) {
        Objects.requireNonNull(createDTO, "Request body cannot be null");
        var movieWithId = createMoviePort.execute(
                createDTO.year(),
                createDTO.title(),
                createDTO.studios(),
                createDTO.producers(),
                createDTO.winner()
        );

        var movieDTO = (MovieDTO) converterDtoPort.toDTO(movieWithId);

        return ResponseEntity.status(HttpStatus.CREATED).body(movieDTO);
    }

    /**
     * Gets a movie by ID.
     *
     * @param id Movie ID
     * @return ResponseEntity with MovieDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovie(@PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id) {
        var movieWithId = getMoviePort.execute(id);
        var movieDTO = (MovieDTO) converterDtoPort.toDTO(movieWithId);
        return ResponseEntity.ok(movieDTO);
    }

    /**
     * Updates an existing movie.
     *
     * @param id Movie ID
     * @param updateDTO Request DTO with updated movie data
     * @return ResponseEntity with updated MovieDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(
            @PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id,
            @RequestBody @Valid UpdateMovieDTO updateDTO) {
        Objects.requireNonNull(updateDTO, "Request body cannot be null");
        var movieWithId = updateMoviePort.execute(
                id,
                updateDTO.year(),
                updateDTO.title(),
                updateDTO.studios(),
                updateDTO.producers(),
                updateDTO.winner()
        );

        var movieDTO = (MovieDTO) converterDtoPort.toDTO(movieWithId);

        return ResponseEntity.ok(movieDTO);
    }

    /**
     * Deletes a movie by ID.
     *
     * @param id Movie ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id) {
        deleteMoviePort.execute(id);
        return ResponseEntity.noContent().build();
    }
}

