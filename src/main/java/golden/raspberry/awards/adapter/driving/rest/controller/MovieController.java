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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;

import golden.raspberry.awards.adapter.driving.rest.controller.constants.MovieControllerConstants;
import golden.raspberry.awards.adapter.driving.rest.controller.constants.ApiIllustrationSetConstants;
import golden.raspberry.awards.core.application.service.PaginationNormalizerService;

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

        this.createMoviePort = Objects.requireNonNull(createMoviePort, MovieControllerConstants.ERROR_MESSAGE_CREATE_MOVIE_PORT_CANNOT_BE_NULL);
        this.getMoviePort = Objects.requireNonNull(getMoviePort, MovieControllerConstants.ERROR_MESSAGE_GET_MOVIE_PORT_CANNOT_BE_NULL);
        this.updateMoviePort = Objects.requireNonNull(updateMoviePort, MovieControllerConstants.ERROR_MESSAGE_UPDATE_MOVIE_PORT_CANNOT_BE_NULL);
        this.deleteMoviePort = Objects.requireNonNull(deleteMoviePort, MovieControllerConstants.ERROR_MESSAGE_DELETE_MOVIE_PORT_CANNOT_BE_NULL);
        this.converterDtoPort = Objects.requireNonNull(converterDtoPort, MovieControllerConstants.ERROR_MESSAGE_CONVERTER_DTO_PORT_CANNOT_BE_NULL);
    }

    @Operation(
            summary = MovieControllerConstants.OPERATION_SUMMARY_CREATE_MOVIE,
            description = MovieControllerConstants.OPERATION_DESCRIPTION_CREATE_MOVIE
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_CREATED_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_MOVIE_CREATED_SUCCESSFULLY,
                    content = @Content(
                            schema = @Schema(implementation = MovieDTO.class),
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_MOVIE_SUCCESS
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_BAD_REQUEST_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_BAD_REQUEST_VALIDATION_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Validation Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_VALIDATION
                            )
                    )
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
            summary = MovieControllerConstants.OPERATION_SUMMARY_GET_ALL_MOVIES,
            description = MovieControllerConstants.OPERATION_DESCRIPTION_GET_ALL_MOVIES
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_SUCCESSFULLY_RETRIEVED_MOVIES,
                    content = @Content(
                            schema = @Schema(implementation = Page.class),
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_MOVIE_LIST_SUCCESS
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_BAD_REQUEST_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_BAD_REQUEST_INVALID_PAGINATION,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Bad Request",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_BAD_REQUEST
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getAll(
            @Parameter(
                    description = MovieControllerConstants.PARAMETER_DESCRIPTION_PAGE_NUMBER,
                    example = MovieControllerConstants.PARAMETER_ILLUSTRATION_SET_PAGE_NUMBER
            )
            @RequestParam(defaultValue = MovieControllerConstants.DEFAULT_PAGE_NUMBER_STRING) int page,
            @Parameter(
                    description = MovieControllerConstants.PARAMETER_DESCRIPTION_PAGE_SIZE,
                    example = MovieControllerConstants.PARAMETER_ILLUSTRATION_SET_PAGE_SIZE
            )
            @RequestParam(defaultValue = MovieControllerConstants.DEFAULT_PAGE_SIZE_STRING) int size,
            @Parameter(
                    description = MovieControllerConstants.PARAMETER_DESCRIPTION_SORT_FIELD,
                    example = MovieControllerConstants.PARAMETER_ILLUSTRATION_SET_SORT_FIELD
            )
            @RequestParam(defaultValue = MovieControllerConstants.DEFAULT_SORT_FIELD) String sort,
            @Parameter(
                    description = MovieControllerConstants.PARAMETER_DESCRIPTION_SORT_DIRECTION,
                    example = MovieControllerConstants.PARAMETER_ILLUSTRATION_SET_SORT_DIRECTION
            )
            @RequestParam(defaultValue = MovieControllerConstants.DEFAULT_SORT_DIRECTION) String direction) {
        
        var normalizedPage = PaginationNormalizerService.normalizePage(page);
        var normalizedSize = PaginationNormalizerService.normalizeSize(size);
        var normalizedDirection = PaginationNormalizerService.normalizeDirection(direction);
        var normalizedSortField = PaginationNormalizerService.normalizeSortField(sort);
        
        var sortDirection = "desc".equalsIgnoreCase(normalizedDirection) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        var pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(sortDirection, normalizedSortField));
        
        var moviePage = getMoviePort.executeAll(pageable);
        var movieDTOs = moviePage.map(this::toMovieDTO);
        
        return ResponseEntity.ok(movieDTOs);
    }

    @Operation(
            summary = MovieControllerConstants.OPERATION_SUMMARY_GET_MOVIE_BY_ID,
            description = MovieControllerConstants.OPERATION_DESCRIPTION_GET_MOVIE_BY_ID
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_MOVIE_FOUND,
                    content = @Content(
                            schema = @Schema(implementation = MovieDTO.class),
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_MOVIE_SUCCESS
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_NOT_FOUND_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_MOVIE_NOT_FOUND,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Not Found",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_NOT_FOUND
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getById(
            @Parameter(
                    description = MovieControllerConstants.PARAMETER_DESCRIPTION_ID,
                    example = MovieControllerConstants.PARAMETER_ILLUSTRATION_SET_ID,
                    required = true
            )
            @PathVariable @Min(value = MovieControllerConstants.VALIDATION_MINIMUM_ID_VALUE, message = MovieControllerConstants.VALIDATION_MESSAGE_PATH_VARIABLE_ID_MUST_BE_POSITIVE_INTEGER) Long id) {
        var movieWithId = getMoviePort.execute(id);
        return ResponseEntity.ok(toMovieDTO(movieWithId));
    }

    @Operation(
            summary = MovieControllerConstants.OPERATION_SUMMARY_UPDATE_MOVIE,
            description = MovieControllerConstants.OPERATION_DESCRIPTION_UPDATE_MOVIE
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_MOVIE_UPDATED_SUCCESSFULLY,
                    content = @Content(
                            schema = @Schema(implementation = MovieDTO.class),
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_MOVIE_SUCCESS
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_BAD_REQUEST_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_BAD_REQUEST_VALIDATION_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Validation Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_VALIDATION
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_NOT_FOUND_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_MOVIE_NOT_FOUND,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Not Found",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_NOT_FOUND
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> update(
            @Parameter(
                    description = "Movie unique identifier",
                    example = MovieControllerConstants.PARAMETER_ILLUSTRATION_SET_ID,
                    required = true
            )
            @PathVariable @Min(value = MovieControllerConstants.VALIDATION_MINIMUM_ID_VALUE, message = MovieControllerConstants.VALIDATION_MESSAGE_PATH_VARIABLE_ID_MUST_BE_POSITIVE_INTEGER) Long id,
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
            summary = MovieControllerConstants.OPERATION_SUMMARY_DELETE_MOVIE,
            description = MovieControllerConstants.OPERATION_DESCRIPTION_DELETE_MOVIE
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_NO_CONTENT_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_MOVIE_DELETED_SUCCESSFULLY
            ),
            @ApiResponse(
                    responseCode = MovieControllerConstants.HTTP_STATUS_CODE_NOT_FOUND_STRING,
                    description = MovieControllerConstants.API_RESPONSE_DESCRIPTION_MOVIE_NOT_FOUND,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Not Found",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_NOT_FOUND
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(
                    description = MovieControllerConstants.PARAMETER_DESCRIPTION_ID,
                    example = MovieControllerConstants.PARAMETER_ILLUSTRATION_SET_ID,
                    required = true
            )
            @PathVariable @Min(value = MovieControllerConstants.VALIDATION_MINIMUM_ID_VALUE, message = MovieControllerConstants.VALIDATION_MESSAGE_PATH_VARIABLE_ID_MUST_BE_POSITIVE_INTEGER) Long id) {
        deleteMoviePort.execute(id);
        return ResponseEntity.noContent().build();
    }

    private MovieDTO toMovieDTO(MovieWithId movieWithId) {
        return (MovieDTO) converterDtoPort.toDTO(movieWithId);
    }
}

