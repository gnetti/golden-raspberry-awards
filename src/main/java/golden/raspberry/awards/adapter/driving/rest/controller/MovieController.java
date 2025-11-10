package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.CreateMovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.UpdateMovieDTO;
import golden.raspberry.awards.core.application.port.in.CreateMoviePort;
import golden.raspberry.awards.core.application.port.in.DeleteMoviePort;
import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.in.UpdateMoviePort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
@Tag(name = "Movies", description = "Movie CRUD operations")
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

    @Operation(
            summary = "Create a new movie",
            description = "Creates a new movie with the provided data. All fields are required and must be valid."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Movie created successfully",
                    content = @Content(schema = @Schema(implementation = MovieDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Validation error or invalid request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<MovieDTO> create(@RequestBody @Valid CreateMovieDTO createDTO) {
        var movieWithId = createMoviePort.execute(
                createDTO.year(),
                createDTO.title(),
                createDTO.studios(),
                createDTO.producers(),
                createDTO.winner()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toMovieDTO(movieWithId));
    }

    @Operation(
            summary = "Get all movies with pagination",
            description = "Returns a paginated list of movies with sorting support. Supports pagination parameters: page (0-based), size, sort (field,direction)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved movies",
                    content = @Content(schema = @Schema(implementation = MovieDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getAll(
            @Parameter(
                    description = "Page number (0-based, default: 0)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "Page size (default: 10, max: 100)",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size,
            @Parameter(
                    description = "Sort field (id, year, title, studios, producers, winner). Default: id",
                    example = "id"
            )
            @RequestParam(defaultValue = "id") String sort,
            @Parameter(
                    description = "Sort direction (asc, desc). Default: asc",
                    example = "asc"
            )
            @RequestParam(defaultValue = "asc") String direction) {
        
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        if (size > 100) size = 100;
        
        var sortDirection = "desc".equalsIgnoreCase(direction) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        var sortField = mapSortField(sort);
        var pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        
        var moviePage = getMoviePort.executeAll(pageable);
        var movieDTOs = moviePage.map(this::toMovieDTO);
        
        return ResponseEntity.ok(movieDTOs);
    }

    private String mapSortField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "year" -> "year";
            case "title" -> "title";
            case "studios" -> "studios";
            case "producers" -> "producers";
            case "winner" -> "winner";
            default -> "id";
        };
    }

    @Operation(
            summary = "Get a movie by ID",
            description = "Retrieves a movie by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movie found",
                    content = @Content(schema = @Schema(implementation = MovieDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movie not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getById(@PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id) {
        var movieWithId = getMoviePort.execute(id);
        return ResponseEntity.ok(toMovieDTO(movieWithId));
    }

    @Operation(
            summary = "Update an existing movie",
            description = "Updates an existing movie with the provided data. All fields are required and must be valid."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movie updated successfully",
                    content = @Content(schema = @Schema(implementation = MovieDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Validation error or invalid request body",
                    content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movie not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> update(
            @PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id,
            @RequestBody @Valid UpdateMovieDTO updateDTO) {
        var movieWithId = updateMoviePort.execute(
                id,
                updateDTO.year(),
                updateDTO.title(),
                updateDTO.studios(),
                updateDTO.producers(),
                updateDTO.winner()
        );
        return ResponseEntity.ok(toMovieDTO(movieWithId));
    }

    @Operation(
            summary = "Delete a movie by ID",
            description = "Deletes a movie by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Movie deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movie not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(value = 1, message = "Path variable 'id' must be a positive integer (>= 1)") Long id) {
        deleteMoviePort.execute(id);
        return ResponseEntity.noContent().build();
    }

    private MovieDTO toMovieDTO(MovieWithId movieWithId) {
        return (MovieDTO) converterDtoPort.toDTO(movieWithId);
    }
}

