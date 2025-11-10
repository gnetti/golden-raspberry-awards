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
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    /**
     * Creates a new movie.
     *
     * @param createDTO Request DTO with movie data
     * @return ResponseEntity with created MovieDTO
     */
    @Operation(
            summary = "Create a new movie",
            description = "Creates a new movie with the provided data. All fields are required and must be valid."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Movie created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MovieDTO.class),
                            examples = @ExampleObject(
                                    name = "Example Response",
                                    value = """
                                            {
                                                "id": 1,
                                                "year": 2024,
                                                "title": "The Matrix Resurrections",
                                                "studios": "Warner Bros. Pictures",
                                                "producers": "Lana Wachowski, Grant Hill",
                                                "winner": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Missing field 'year'",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing year",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:56:31.7641082",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'year' is missing from request body",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Invalid year value (< 1900)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid year value",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:57:08.5861873",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'year' has invalid value: Field 'year' must be at least 1900",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid or missing 'year' field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Year blank or invalid type",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:57:36.3791117",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Field 'year' has invalid or missing value. Expected type: integer. Valid values: a number between 1900 and 2100 (e.g., 2024).",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Missing field 'title'",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing title",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:58:18.3145605",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'title' is missing from request body; Field 'title' is missing from request body",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Invalid 'title' value (blank or too short)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid title",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:58:18.3145605",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'title' is missing from request body; Field 'title' is missing from request body",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - 'title' too short (< 2 characters)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Title too short",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:59:17.4353748",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'title' has invalid value: Field 'title' must be between 2 and 255 characters",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Missing field 'studios'",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing studios",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:59:45.7867061",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'studios' is missing from request body; Field 'studios' is missing from request body",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Invalid 'studios' value (blank or too short)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid studios",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:00:10.1311708",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'studios' has invalid value: Field 'studios' cannot be empty or contain only whitespace; Field 'studios' has invalid value: Field 'studios' must be between 2 and 255 characters",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - 'studios' too short (< 2 characters)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Studios too short",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:00:38.7789359",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'studios' has invalid value: Field 'studios' must be between 2 and 255 characters",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Missing field 'producers'",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing producers",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:01:05.6605782",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'producers' is missing from request body; Field 'producers' is missing from request body",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Invalid 'producers' value (blank or too short)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid producers",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:01:25.8270079",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'producers' has invalid value: Field 'producers' must be between 2 and 255 characters; Field 'producers' has invalid value: Field 'producers' cannot be empty or contain only whitespace",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - 'producers' too short (< 2 characters)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Producers too short",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:01:49.1148144",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'producers' has invalid value: Field 'producers' must be between 2 and 255 characters",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Missing or invalid 'winner' field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing or blank winner",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:02:16.7207579",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Field 'winner' has invalid or missing value. Expected type: boolean. Valid values: true or false (e.g., true).",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid 'winner' value type (not boolean)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid winner type",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:12:48.4597322",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Field 'winner' has invalid or missing value. Expected type: boolean. Valid values: true or false (e.g., true).",
                                                "path": "/api/movies"
                                            }
                                            """
                            )
                    )
            )
    })
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
     * Gets all movies.
     *
     * @return ResponseEntity with list of MovieDTO
     */
    @Operation(
            summary = "Get all movies",
            description = "Returns a list of all movies in the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all movies",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MovieDTO.class),
                            examples = @ExampleObject(
                                    name = "Example Response",
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "year": 2024,
                                                    "title": "The Matrix Resurrections",
                                                    "studios": "Warner Bros. Pictures",
                                                    "producers": "Lana Wachowski, Grant Hill",
                                                    "winner": true
                                                },
                                                {
                                                    "id": 2,
                                                    "year": 2023,
                                                    "title": "Another Movie",
                                                    "studios": "Universal Pictures",
                                                    "producers": "John Doe",
                                                    "winner": false
                                                }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<java.util.List<MovieDTO>> getAllMovies() {
        var moviesWithId = getMoviePort.executeAll();
        var movieDTOs = moviesWithId.stream()
                .map(movie -> (MovieDTO) converterDtoPort.toDTO(movie))
                .toList();
        return ResponseEntity.ok(movieDTOs);
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
    @Operation(
            summary = "Update an existing movie",
            description = "Updates an existing movie with the provided data. All fields are required and must be valid."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Movie updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MovieDTO.class),
                            examples = @ExampleObject(
                                    name = "Example Response",
                                    value = """
                                            {
                                                "id": 1,
                                                "year": 2024,
                                                "title": "The Matrix Resurrections",
                                                "studios": "Warner Bros. Pictures",
                                                "producers": "Lana Wachowski, Grant Hill",
                                                "winner": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Missing field 'year'",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing year",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:56:31.7641082",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'year' is missing from request body",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Invalid year value (< 1900)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid year value",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:57:08.5861873",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'year' has invalid value: Field 'year' must be at least 1900",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid or missing 'year' field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Year blank or invalid type",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:57:36.3791117",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Field 'year' has invalid or missing value. Expected type: integer. Valid values: a number between 1900 and 2100 (e.g., 2024).",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Missing field 'title'",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing title",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:58:18.3145605",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'title' is missing from request body; Field 'title' is missing from request body",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Invalid 'title' value (blank or too short)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid title",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:58:18.3145605",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'title' is missing from request body; Field 'title' is missing from request body",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - 'title' too short (< 2 characters)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Title too short",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:59:17.4353748",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'title' has invalid value: Field 'title' must be between 2 and 255 characters",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Missing field 'studios'",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing studios",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T15:59:45.7867061",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'studios' is missing from request body; Field 'studios' is missing from request body",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Invalid 'studios' value (blank or too short)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid studios",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:00:10.1311708",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'studios' has invalid value: Field 'studios' cannot be empty or contain only whitespace; Field 'studios' has invalid value: Field 'studios' must be between 2 and 255 characters",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - 'studios' too short (< 2 characters)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Studios too short",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:00:38.7789359",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'studios' has invalid value: Field 'studios' must be between 2 and 255 characters",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Missing field 'producers'",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing producers",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:01:05.6605782",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'producers' is missing from request body; Field 'producers' is missing from request body",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - Invalid 'producers' value (blank or too short)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid producers",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:01:25.8270079",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Validation failed with 2 error(s): Field 'producers' has invalid value: Field 'producers' must be between 2 and 255 characters; Field 'producers' has invalid value: Field 'producers' cannot be empty or contain only whitespace",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation Error - 'producers' too short (< 2 characters)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Producers too short",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:01:49.1148144",
                                                "status": 400,
                                                "error": "Validation Error",
                                                "message": "Field 'producers' has invalid value: Field 'producers' must be between 2 and 255 characters",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Missing or invalid 'winner' field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Missing or blank winner",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:02:16.7207579",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Field 'winner' has invalid or missing value. Expected type: boolean. Valid values: true or false (e.g., true).",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid 'winner' value type (not boolean)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Invalid winner type",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T16:12:48.4597322",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Field 'winner' has invalid or missing value. Expected type: boolean. Valid values: true or false (e.g., true).",
                                                "path": "/api/movies/1"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movie not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Movie not found",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T11:50:15.3132801",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Movie with ID 250 not found",
                                                "path": "/api/movies/250"
                                            }
                                            """
                            )
                    )
            )
    })
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

