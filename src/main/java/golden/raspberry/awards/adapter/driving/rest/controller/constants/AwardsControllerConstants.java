package golden.raspberry.awards.adapter.driving.rest.controller.constants;

/**
 * Constants for AwardsController REST API endpoints.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class AwardsControllerConstants {

    private AwardsControllerConstants() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static final String ERROR_MESSAGE_CALCULATE_INTERVALS_PORT_CANNOT_BE_NULL = "CalculateIntervalsPort cannot be null";
    public static final String ERROR_MESSAGE_CONVERTER_DTO_PORT_CANNOT_BE_NULL = "ConverterDtoPort cannot be null";

    public static final String HTTP_STATUS_CODE_OK_STRING = "200";
    public static final String HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING = "500";

    public static final String OPERATION_SUMMARY_GET_PRODUCER_INTERVALS = "Get producer intervals";
    public static final String OPERATION_DESCRIPTION_GET_PRODUCER_INTERVALS = 
            "Returns producers with the minimum and maximum intervals between consecutive awards";

    public static final String API_RESPONSE_DESCRIPTION_SUCCESSFULLY_RETRIEVED_INTERVALS = "Successfully retrieved producer intervals";
    public static final String API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR = "Internal Server Error";
}

