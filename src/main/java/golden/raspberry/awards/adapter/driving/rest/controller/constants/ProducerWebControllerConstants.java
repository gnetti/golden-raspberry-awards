package golden.raspberry.awards.adapter.driving.rest.controller.constants;

/**
 * Constants for ProducerWebController web endpoints.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class ProducerWebControllerConstants {

    private ProducerWebControllerConstants() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static final String ERROR_MESSAGE_CALCULATE_INTERVALS_PORT_CANNOT_BE_NULL = "CalculateIntervalsPort cannot be null";
    public static final String ERROR_MESSAGE_CONVERTER_DTO_PORT_CANNOT_BE_NULL = "ConverterDtoPort cannot be null";
    public static final String ERROR_MESSAGE_GET_MOVIE_PORT_CANNOT_BE_NULL = "GetMoviePort cannot be null";
    public static final String ERROR_MESSAGE_GET_MOVIES_FOR_WEB_PORT_CANNOT_BE_NULL = "GetMoviesForWebPort cannot be null";

    public static final String HTTP_STATUS_CODE_OK_STRING = "200";
    public static final String HTTP_STATUS_CODE_FOUND_STRING = "302";
    public static final String HTTP_STATUS_CODE_BAD_REQUEST_STRING = "400";
    public static final String HTTP_STATUS_CODE_NOT_FOUND_STRING = "404";
    public static final String HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING = "500";

    public static final String OPERATION_SUMMARY_REDIRECT_TO_DASHBOARD = "Redirect to dashboard";
    public static final String OPERATION_DESCRIPTION_REDIRECT_TO_DASHBOARD = "Redirects root path (/) to /dashboard";

    public static final String OPERATION_SUMMARY_GET_MANUAL = "Get system manual page";
    public static final String OPERATION_DESCRIPTION_GET_MANUAL = "Returns the system manual page with complete documentation";

    public static final String OPERATION_SUMMARY_GET_DASHBOARD = "Get dashboard page";
    public static final String OPERATION_DESCRIPTION_GET_DASHBOARD = "Returns the main dashboard page with API information and navigation cards";

    public static final String OPERATION_SUMMARY_GET_INTERVALS = "Get producer intervals page";
    public static final String OPERATION_DESCRIPTION_GET_INTERVALS = "Returns the producer intervals page showing minimum and maximum intervals between consecutive awards";

    public static final String OPERATION_SUMMARY_GET_MOVIES = "Get movies list page";
    public static final String OPERATION_DESCRIPTION_GET_MOVIES = "Returns the movies list page with pagination, sorting, and filtering support";

    public static final String OPERATION_SUMMARY_GET_NEW_MOVIE = "Get new movie form page";
    public static final String OPERATION_DESCRIPTION_GET_NEW_MOVIE = "Returns the new movie creation form page";

    public static final String OPERATION_SUMMARY_GET_EDIT_MOVIE = "Get edit movie form page";
    public static final String OPERATION_DESCRIPTION_GET_EDIT_MOVIE = "Returns the edit movie form page for a specific movie by ID";

    public static final String API_RESPONSE_DESCRIPTION_SUCCESS = "Page rendered successfully";
    public static final String API_RESPONSE_DESCRIPTION_REDIRECT = "Redirect to dashboard";
    public static final String API_RESPONSE_DESCRIPTION_BAD_REQUEST = "Invalid request parameters";
    public static final String API_RESPONSE_DESCRIPTION_NOT_FOUND = "Movie not found";
    public static final String API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR = "Internal server error";

    public static final String PARAMETER_DESCRIPTION_PAGE = "Page number (0-based)";
    public static final String PARAMETER_ILLUSTRATION_SET_PAGE = "0";
    public static final String PARAMETER_DESCRIPTION_SIZE = "Page size (number of items per page)";
    public static final String PARAMETER_ILLUSTRATION_SET_SIZE = "10";
    public static final String PARAMETER_DESCRIPTION_SORT_BY = "Field to sort by (id, year, title, studios, producers, winner)";
    public static final String PARAMETER_ILLUSTRATION_SET_SORT_BY = "id";
    public static final String PARAMETER_DESCRIPTION_DIRECTION = "Sort direction (asc or desc)";
    public static final String PARAMETER_ILLUSTRATION_SET_DIRECTION = "asc";
    public static final String PARAMETER_DESCRIPTION_FILTER_TYPE = "Type of filter (id, year, title, studios, producers, all)";
    public static final String PARAMETER_ILLUSTRATION_SET_FILTER_TYPE = "id";
    public static final String PARAMETER_DESCRIPTION_FILTER_VALUE = "Value to filter by";
    public static final String PARAMETER_ILLUSTRATION_SET_FILTER_VALUE = "1";
    public static final String PARAMETER_DESCRIPTION_MODAL = "Whether to return only modal content (no layout)";
    public static final String PARAMETER_ILLUSTRATION_SET_MODAL = "false";
    public static final String PARAMETER_DESCRIPTION_ID = "Movie unique identifier";
    public static final String PARAMETER_ILLUSTRATION_SET_ID = "1";

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_DIRECTION = "asc";
}

