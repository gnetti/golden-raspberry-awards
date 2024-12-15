package golden.raspberry.awards.adapter.driving.rest.controller.constants;

/**
 * Constants for JavadocController documentation endpoints.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class JavadocControllerConstants {

    private JavadocControllerConstants() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static final String ERROR_MESSAGE_HTTP_SERVLET_RESPONSE_CANNOT_BE_NULL = "HttpServletResponse cannot be null";

    public static final String HTTP_STATUS_CODE_FOUND_STRING = "302";
    public static final String HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING = "500";

    public static final String OPERATION_SUMMARY_REDIRECT_TO_JAVADOC = "Redirect to JavaDoc documentation";
    public static final String OPERATION_DESCRIPTION_REDIRECT_TO_JAVADOC = "Redirects to the JavaDoc index page at /docs/index.html";

    public static final String API_RESPONSE_DESCRIPTION_REDIRECT = "Redirect to JavaDoc index page";
    public static final String API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR = "Internal server error";
}

