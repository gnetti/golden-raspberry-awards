package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.DocumentInfoDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.controller.constants.ProducerWebControllerConstants;
import golden.raspberry.awards.adapter.driving.rest.controller.constants.ApiIllustrationSetConstants;
import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.in.GetMoviesForWebPort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import golden.raspberry.awards.core.application.service.YearService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

/**
 * Web Controller for Thymeleaf pages.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Controller
@Tag(name = "Web Pages", description = "Thymeleaf web page endpoints")
public class ProducerWebController {

    private final CalculateIntervalsPort calculateIntervalsPort;
    private final ConverterDtoPort converterDtoPort;
    private final GetMoviePort getMoviePort;
    private final GetMoviesForWebPort getMoviesForWebPort;

    public ProducerWebController(
            CalculateIntervalsPort calculateIntervalsPort,
            ConverterDtoPort converterDtoPort,
            GetMoviePort getMoviePort,
            GetMoviesForWebPort getMoviesForWebPort) {
        this.calculateIntervalsPort = Objects.requireNonNull(
                calculateIntervalsPort,
                ProducerWebControllerConstants.ERROR_MESSAGE_CALCULATE_INTERVALS_PORT_CANNOT_BE_NULL
        );
        this.converterDtoPort = Objects.requireNonNull(
                converterDtoPort,
                ProducerWebControllerConstants.ERROR_MESSAGE_CONVERTER_DTO_PORT_CANNOT_BE_NULL
        );
        this.getMoviePort = Objects.requireNonNull(
                getMoviePort,
                ProducerWebControllerConstants.ERROR_MESSAGE_GET_MOVIE_PORT_CANNOT_BE_NULL
        );
        this.getMoviesForWebPort = Objects.requireNonNull(
                getMoviesForWebPort,
                ProducerWebControllerConstants.ERROR_MESSAGE_GET_MOVIES_FOR_WEB_PORT_CANNOT_BE_NULL
        );
    }

    /**
     * Redirects root path to dashboard.
     *
     * @return Redirect to "/dashboard"
     */
    @Operation(
            summary = ProducerWebControllerConstants.OPERATION_SUMMARY_REDIRECT_TO_DASHBOARD,
            description = ProducerWebControllerConstants.OPERATION_DESCRIPTION_REDIRECT_TO_DASHBOARD
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_FOUND_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_REDIRECT
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    /**
     * Handles GET request for manual page.
     *
     * @return View name "pages/manual"
     */
    @Operation(
            summary = ProducerWebControllerConstants.OPERATION_SUMMARY_GET_MANUAL,
            description = ProducerWebControllerConstants.OPERATION_DESCRIPTION_GET_MANUAL
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_SUCCESS
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping("/manual")
    public String manual() {
        return "pages/manual";
    }

    /**
     * Handles GET request for dashboard page.
     *
     * @param model Model to add attributes for Thymeleaf
     * @return View name "pages/dashboard"
     */
    @Operation(
            summary = ProducerWebControllerConstants.OPERATION_SUMMARY_GET_DASHBOARD,
            description = ProducerWebControllerConstants.OPERATION_DESCRIPTION_GET_DASHBOARD
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_SUCCESS
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var docInfo = DocumentInfoDTO.createDefault();
        
        model.addAttribute("title", "Dashboard");
        model.addAttribute("apiVersion", docInfo.apiVersion());
        model.addAttribute("baseUrl", docInfo.baseUrl());
        model.addAttribute("description", docInfo.description());
        model.addAttribute("architecture", docInfo.architecture());
        model.addAttribute("maturityLevel", docInfo.maturityLevel());
        model.addAttribute("author", docInfo.author());
        
        return "pages/dashboard";
    }

    /**
     * Handles GET request for intervals page.
     *
     * @param model Model to add attributes for Thymeleaf
     * @param modal Whether to return only the content for modal (no layout)
     * @return View name "pages/intervals" or "fragments/intervals-modal" if modal
     */
    @Operation(
            summary = ProducerWebControllerConstants.OPERATION_SUMMARY_GET_INTERVALS,
            description = ProducerWebControllerConstants.OPERATION_DESCRIPTION_GET_INTERVALS
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_SUCCESS
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_BAD_REQUEST_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Bad Request",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_BAD_REQUEST
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping("/intervals")
    public String intervals(
            Model model,
            @Parameter(
                    description = ProducerWebControllerConstants.PARAMETER_DESCRIPTION_MODAL,
                    example = ProducerWebControllerConstants.PARAMETER_ILLUSTRATION_SET_MODAL
            )
            @RequestParam(required = false) Boolean modal) {
        var response = calculateIntervalsPort.execute();
        var dto = converterDtoPort.toDTO(response);
        model.addAttribute("intervals", dto);
        model.addAttribute("title", "Producer Intervals");
        
        if (Boolean.TRUE.equals(modal)) {
            return "fragments/intervals-modal";
        }
        
        return "pages/intervals";
    }

    @Operation(
            summary = ProducerWebControllerConstants.OPERATION_SUMMARY_GET_MOVIES,
            description = ProducerWebControllerConstants.OPERATION_DESCRIPTION_GET_MOVIES
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_SUCCESS
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_BAD_REQUEST_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_BAD_REQUEST,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Bad Request",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_BAD_REQUEST
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping("/movies")
    public String movies(
            Model model,
            @Parameter(
                    description = ProducerWebControllerConstants.PARAMETER_DESCRIPTION_PAGE,
                    example = ProducerWebControllerConstants.PARAMETER_ILLUSTRATION_SET_PAGE
            )
            @RequestParam(defaultValue = ProducerWebControllerConstants.DEFAULT_PAGE) int page,
            @Parameter(
                    description = ProducerWebControllerConstants.PARAMETER_DESCRIPTION_SIZE,
                    example = ProducerWebControllerConstants.PARAMETER_ILLUSTRATION_SET_SIZE
            )
            @RequestParam(defaultValue = ProducerWebControllerConstants.DEFAULT_SIZE) int size,
            @Parameter(
                    description = ProducerWebControllerConstants.PARAMETER_DESCRIPTION_SORT_BY,
                    example = ProducerWebControllerConstants.PARAMETER_ILLUSTRATION_SET_SORT_BY
            )
            @RequestParam(defaultValue = ProducerWebControllerConstants.DEFAULT_SORT_BY) String sortBy,
            @Parameter(
                    description = ProducerWebControllerConstants.PARAMETER_DESCRIPTION_DIRECTION,
                    example = ProducerWebControllerConstants.PARAMETER_ILLUSTRATION_SET_DIRECTION
            )
            @RequestParam(defaultValue = ProducerWebControllerConstants.DEFAULT_DIRECTION) String direction,
            @Parameter(
                    description = ProducerWebControllerConstants.PARAMETER_DESCRIPTION_FILTER_TYPE,
                    example = ProducerWebControllerConstants.PARAMETER_ILLUSTRATION_SET_FILTER_TYPE
            )
            @RequestParam(required = false) String filterType,
            @Parameter(
                    description = ProducerWebControllerConstants.PARAMETER_DESCRIPTION_FILTER_VALUE,
                    example = ProducerWebControllerConstants.PARAMETER_ILLUSTRATION_SET_FILTER_VALUE
            )
            @RequestParam(required = false) String filterValue) {
        
        var request = GetMoviesForWebPort.MoviesWebRequest.normalize(
                page,
                size,
                sortBy,
                direction,
                filterType,
                filterValue
        );
        
        var response = getMoviesForWebPort.execute(request);
        
        model.addAttribute("movies", response.movies());
        model.addAttribute("currentPage", response.currentPage());
        model.addAttribute("totalPages", response.totalPages());
        model.addAttribute("totalItems", response.totalItems());
        model.addAttribute("pageSize", response.pageSize());
        model.addAttribute("sortBy", response.sortBy());
        model.addAttribute("direction", response.direction());
        model.addAttribute("pageNumbers", response.pageNumbers());
        model.addAttribute("filterType", response.filterType());
        model.addAttribute("filterValue", response.filterValue());
        model.addAttribute("title", "Movies");
        model.addAttribute("currentYear", YearService.getCurrentYear());
        
        return "pages/movies";
    }

    /**
     * Handles GET request for new movie page.
     *
     * @param model Model to add attributes for Thymeleaf
     * @return View name "pages/new-movie"
     */
    @Operation(
            summary = ProducerWebControllerConstants.OPERATION_SUMMARY_GET_NEW_MOVIE,
            description = ProducerWebControllerConstants.OPERATION_DESCRIPTION_GET_NEW_MOVIE
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_SUCCESS
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping("/movies/new")
    public String newMovie(Model model) {
        model.addAttribute("title", "New Movie");
        model.addAttribute("currentYear", YearService.getCurrentYear());
        return "pages/new-movie";
    }

    @Operation(
            summary = ProducerWebControllerConstants.OPERATION_SUMMARY_GET_EDIT_MOVIE,
            description = ProducerWebControllerConstants.OPERATION_DESCRIPTION_GET_EDIT_MOVIE
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_SUCCESS
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_NOT_FOUND_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_NOT_FOUND,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Not Found",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_NOT_FOUND
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = ProducerWebControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = ProducerWebControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping("/movies/{id}/edit")
    public String editMovie(
            @Parameter(
                    description = ProducerWebControllerConstants.PARAMETER_DESCRIPTION_ID,
                    example = ProducerWebControllerConstants.PARAMETER_ILLUSTRATION_SET_ID
            )
            @PathVariable Long id,
            Model model) {
        var movieWithId = getMoviePort.execute(id);
        var movieDTO = (MovieDTO) converterDtoPort.toDTO(movieWithId);
        
        model.addAttribute("title", "Edit Movie");
        model.addAttribute("currentYear", YearService.getCurrentYear());
        model.addAttribute("movie", movieDTO);
        model.addAttribute("movieId", movieDTO.id());
        
        return "pages/edit-movie";
    }
}

