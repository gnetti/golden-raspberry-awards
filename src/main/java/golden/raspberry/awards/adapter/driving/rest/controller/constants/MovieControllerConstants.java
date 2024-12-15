package golden.raspberry.awards.adapter.driving.rest.controller.constants;

/**
 * Constants class for MovieController.
 * Contains all constant values used in the Movie REST API endpoints.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieControllerConstants {

    private MovieControllerConstants() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    // ==================== Dependency Injection Messages ====================
    public static final String ERROR_MESSAGE_CREATE_MOVIE_PORT_CANNOT_BE_NULL = "CreateMoviePort cannot be null";
    public static final String ERROR_MESSAGE_GET_MOVIE_PORT_CANNOT_BE_NULL = "GetMoviePort cannot be null";
    public static final String ERROR_MESSAGE_UPDATE_MOVIE_PORT_CANNOT_BE_NULL = "UpdateMoviePort cannot be null";
    public static final String ERROR_MESSAGE_DELETE_MOVIE_PORT_CANNOT_BE_NULL = "DeleteMoviePort cannot be null";
    public static final String ERROR_MESSAGE_CONVERTER_DTO_PORT_CANNOT_BE_NULL = "ConverterDtoPort cannot be null";

    // ==================== Pagination Constants ====================
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int MINIMUM_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MINIMUM_PAGE_SIZE = 1;
    public static final int MAXIMUM_PAGE_SIZE = 100;
    
    // Pagination defaults as String constants for annotations (must be compile-time constants)
    public static final String DEFAULT_PAGE_NUMBER_STRING = "0";
    public static final String DEFAULT_PAGE_SIZE_STRING = "10";

    // ==================== Sorting Constants ====================
    public static final String DEFAULT_SORT_FIELD = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    public static final String SORT_DIRECTION_DESCENDING = "desc";
    
    // Sort field names
    public static final String SORT_FIELD_ID = "id";
    public static final String SORT_FIELD_YEAR = "year";
    public static final String SORT_FIELD_TITLE = "title";
    public static final String SORT_FIELD_STUDIOS = "studios";
    public static final String SORT_FIELD_PRODUCERS = "producers";
    public static final String SORT_FIELD_WINNER = "winner";

    // ==================== HTTP Status Codes ====================
    public static final int HTTP_STATUS_CODE_CREATED = 201;
    public static final int HTTP_STATUS_CODE_OK = 200;
    public static final int HTTP_STATUS_CODE_BAD_REQUEST = 400;
    public static final int HTTP_STATUS_CODE_NOT_FOUND = 404;
    public static final int HTTP_STATUS_CODE_NO_CONTENT = 204;
    
    // HTTP Status Codes as String constants for annotations (must be compile-time constants)
    public static final String HTTP_STATUS_CODE_CREATED_STRING = "201";
    public static final String HTTP_STATUS_CODE_OK_STRING = "200";
    public static final String HTTP_STATUS_CODE_BAD_REQUEST_STRING = "400";
    public static final String HTTP_STATUS_CODE_NOT_FOUND_STRING = "404";
    public static final String HTTP_STATUS_CODE_NO_CONTENT_STRING = "204";

    // ==================== Validation Messages ====================
    public static final String VALIDATION_MESSAGE_PATH_VARIABLE_ID_MUST_BE_POSITIVE_INTEGER = 
            "Path variable 'id' must be a positive integer (>= 1)";
    public static final int VALIDATION_MINIMUM_ID_VALUE = 1;

    // ==================== Operation Descriptions ====================
    public static final String OPERATION_SUMMARY_CREATE_MOVIE = "Create a new movie";
    public static final String OPERATION_DESCRIPTION_CREATE_MOVIE = 
            "Creates a new movie with the provided data. All fields are required and must be valid.";
    
    public static final String OPERATION_SUMMARY_GET_ALL_MOVIES = "Get all movies with pagination";
    public static final String OPERATION_DESCRIPTION_GET_ALL_MOVIES = 
            "Returns a paginated list of movies with sorting support. Supports pagination parameters: page (0-based), size, sort (field,direction).";
    
    public static final String OPERATION_SUMMARY_GET_MOVIE_BY_ID = "Get a movie by ID";
    public static final String OPERATION_DESCRIPTION_GET_MOVIE_BY_ID = 
            "Retrieves a movie by its unique identifier";
    
    public static final String OPERATION_SUMMARY_UPDATE_MOVIE = "Update an existing movie";
    public static final String OPERATION_DESCRIPTION_UPDATE_MOVIE = 
            "Updates an existing movie with the provided data. All fields are required and must be valid.";
    
    public static final String OPERATION_SUMMARY_DELETE_MOVIE = "Delete a movie by ID";
    public static final String OPERATION_DESCRIPTION_DELETE_MOVIE = 
            "Deletes a movie by its unique identifier";

    // ==================== API Response Messages ====================
    public static final String API_RESPONSE_DESCRIPTION_MOVIE_CREATED_SUCCESSFULLY = "Movie created successfully";
    public static final String API_RESPONSE_DESCRIPTION_BAD_REQUEST_VALIDATION_ERROR = 
            "Bad Request - Validation error or invalid request body";
    public static final String API_RESPONSE_DESCRIPTION_SUCCESSFULLY_RETRIEVED_MOVIES = "Successfully retrieved movies";
    public static final String API_RESPONSE_DESCRIPTION_BAD_REQUEST_INVALID_PAGINATION = 
            "Bad Request - Invalid pagination parameters";
    public static final String API_RESPONSE_DESCRIPTION_MOVIE_FOUND = "Movie found";
    public static final String API_RESPONSE_DESCRIPTION_MOVIE_NOT_FOUND = "Movie not found";
    public static final String API_RESPONSE_DESCRIPTION_MOVIE_UPDATED_SUCCESSFULLY = "Movie updated successfully";
    public static final String API_RESPONSE_DESCRIPTION_MOVIE_DELETED_SUCCESSFULLY = "Movie deleted successfully";

    // ==================== Parameter Descriptions ====================
    public static final String PARAMETER_DESCRIPTION_PAGE_NUMBER = "Page number (0-based, default: 0)";
    public static final String PARAMETER_EXAMPLE_PAGE_NUMBER = "0";
    public static final String PARAMETER_DESCRIPTION_PAGE_SIZE = "Page size (default: 10, max: 100)";
    public static final String PARAMETER_EXAMPLE_PAGE_SIZE = "10";
    public static final String PARAMETER_DESCRIPTION_SORT_FIELD = 
            "Sort field (id, year, title, studios, producers, winner). Default: id";
    public static final String PARAMETER_EXAMPLE_SORT_FIELD = "id";
    public static final String PARAMETER_DESCRIPTION_SORT_DIRECTION = "Sort direction (asc, desc). Default: asc";
    public static final String PARAMETER_EXAMPLE_SORT_DIRECTION = "asc";
}

