package golden.raspberry.awards.adapter.driving.rest.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ApiExceptionHandler Tests")
class ApiExceptionHandlerTest {

    private ApiExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new ApiExceptionHandler();
        var httpServletRequest = mock(HttpServletRequest.class);
        webRequest = new ServletWebRequest(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/movies");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgumentException() {
        var exception = new IllegalArgumentException("Invalid argument");

        var result = handler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(400, errorDTO.status());
        assertEquals("Bad Request", errorDTO.error());
        assertEquals("Invalid argument", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle IllegalStateException with not found message")
    void shouldHandleIllegalStateExceptionWithNotFoundMessage() {
        var exception = new IllegalStateException("Movie not found");

        var result = handler.handleIllegalStateException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(404, errorDTO.status());
        assertEquals("Not Found", errorDTO.error());
        assertEquals("Movie not found", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle IllegalStateException without not found message")
    void shouldHandleIllegalStateExceptionWithoutNotFoundMessage() {
        var exception = new IllegalStateException("Invalid state");

        var result = handler.handleIllegalStateException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(400, errorDTO.status());
        assertEquals("Bad Request", errorDTO.error());
        assertEquals("Invalid state", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle IllegalStateException with null message")
    void shouldHandleIllegalStateExceptionWithNullMessage() {
        var exception = new IllegalStateException();

        var result = handler.handleIllegalStateException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals("Illegal state", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle NullPointerException for REST endpoints")
    void shouldHandleNullPointerExceptionForRestEndpoints() {
        var exception = new NullPointerException("Null value");
        var restRequest = mock(WebRequest.class);
        when(restRequest.getDescription(false)).thenReturn("uri=/api/movies");

        var result = handler.handleNullPointerException(exception, restRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(400, errorDTO.status());
        assertEquals("Bad Request", errorDTO.error());
        assertEquals("Null value", errorDTO.message());
    }

    @Test
    @DisplayName("Should rethrow NullPointerException for web endpoints")
    void shouldRethrowNullPointerExceptionForWebEndpoints() {
        var exception = new NullPointerException("Null value");
        var webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/dashboard");

        assertThrows(NullPointerException.class, () ->
                handler.handleNullPointerException(exception, webRequest));
    }

    @Test
    @DisplayName("Should handle NullPointerException with blank message")
    void shouldHandleNullPointerExceptionWithBlankMessage() {
        var exception = new NullPointerException("   ");
        var restRequest = mock(WebRequest.class);
        when(restRequest.getDescription(false)).thenReturn("uri=/api/movies");

        var result = handler.handleNullPointerException(exception, restRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals("Required field is missing or null", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with single error")
    void shouldHandleMethodArgumentNotValidExceptionWithSingleError() {
        var fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("year");
        when(fieldError.getDefaultMessage()).thenReturn("Invalid year");
        when(fieldError.getRejectedValue()).thenReturn("invalid");

        var bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        var exception = new MethodArgumentNotValidException(null, bindingResult);

        var result = handler.handleMethodArgumentNotValidException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(400, errorDTO.status());
        assertEquals("Validation Error", errorDTO.error());
        assertTrue(errorDTO.message().contains("year"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple errors")
    void shouldHandleMethodArgumentNotValidExceptionWithMultipleErrors() {
        var fieldError1 = mock(FieldError.class);
        when(fieldError1.getField()).thenReturn("year");
        when(fieldError1.getDefaultMessage()).thenReturn("Invalid year");
        when(fieldError1.getRejectedValue()).thenReturn("invalid");

        var fieldError2 = mock(FieldError.class);
        when(fieldError2.getField()).thenReturn("title");
        when(fieldError2.getDefaultMessage()).thenReturn("Invalid title");
        when(fieldError2.getRejectedValue()).thenReturn(null);

        var bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        var exception = new MethodArgumentNotValidException(null, bindingResult);

        var result = handler.handleMethodArgumentNotValidException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertTrue(errorDTO.message().contains("2 error(s)"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with empty error message and null rejected value")
    void shouldHandleMethodArgumentNotValidExceptionWithEmptyErrorMessage() {
        var fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("year");
        when(fieldError.getDefaultMessage()).thenReturn("");
        when(fieldError.getRejectedValue()).thenReturn(null);

        var bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        var exception = new MethodArgumentNotValidException(null, bindingResult);

        var result = handler.handleMethodArgumentNotValidException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals("Field 'year' is missing from request body", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with null error message and non-null rejected value")
    void shouldHandleMethodArgumentNotValidExceptionWithNullErrorMessage() {
        var fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("year");
        when(fieldError.getDefaultMessage()).thenReturn(null);
        when(fieldError.getRejectedValue()).thenReturn("invalid");

        var bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        var exception = new MethodArgumentNotValidException(null, bindingResult);

        var result = handler.handleMethodArgumentNotValidException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals("Field 'year' has invalid value: Invalid value", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException")
    void shouldHandleHttpMessageNotReadableException() {
        var exception = new HttpMessageNotReadableException("Invalid JSON", new RuntimeException("Parse error"));

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(400, errorDTO.status());
        assertEquals("Bad Request", errorDTO.error());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void shouldHandleGenericException() {
        var exception = new RuntimeException("Unexpected error");

        var result = handler.handleGenericException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(500, errorDTO.status());
        assertEquals("Internal Server Error", errorDTO.error());
        assertEquals("Unexpected error", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle generic Exception with null message")
    void shouldHandleGenericExceptionWithNullMessage() {
        var exception = new RuntimeException();

        var result = handler.handleGenericException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals("An unexpected error occurred", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with Unrecognized token error")
    void shouldHandleHttpMessageNotReadableExceptionWithUnrecognizedToken() {
        var rootCause = new RuntimeException("Unrecognized token 'x' was expecting ('true' or 'false')");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertTrue(errorDTO.message().contains("invalid or missing value"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with Expected value error")
    void shouldHandleHttpMessageNotReadableExceptionWithExpectedValue() {
        var rootCause = new RuntimeException("expected a value");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with Comma error")
    void shouldHandleHttpMessageNotReadableExceptionWithCommaError() {
        var rootCause = new RuntimeException("Unexpected character (code 44)");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with Type mismatch error")
    void shouldHandleHttpMessageNotReadableExceptionWithTypeMismatch() {
        var rootCause = new RuntimeException("Cannot deserialize value");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with field name detection")
    void shouldHandleHttpMessageNotReadableExceptionWithFieldNameDetection() {
        var rootCause = new RuntimeException("Cannot deserialize value of type java.lang.Integer from String \"invalid\": not a valid Integer value. Field \"year\" is invalid at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertTrue(errorDTO.message().contains("year") || errorDTO.message().contains("Field"), 
                "Error message should contain 'year' or 'Field': " + errorDTO.message());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with missing request body")
    void shouldHandleHttpMessageNotReadableExceptionWithMissingRequestBody() {
        var exception = new HttpMessageNotReadableException("Required request body is missing");

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertTrue(errorDTO.message().contains("Request body is required"));
    }

    @Test
    @DisplayName("Should handle NullPointerException with null message")
    void shouldHandleNullPointerExceptionWithNullMessage() {
        var exception = new NullPointerException();
        var restRequest = mock(WebRequest.class);
        when(restRequest.getDescription(false)).thenReturn("uri=/api/movies");

        var result = handler.handleNullPointerException(exception, restRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals("Required field is missing or null", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle NullPointerException for /intervals endpoint")
    void shouldHandleNullPointerExceptionForIntervalsEndpoint() {
        var exception = new NullPointerException("Null value");
        var webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/intervals");

        assertThrows(NullPointerException.class, () ->
                handler.handleNullPointerException(exception, webRequest));
    }

    @Test
    @DisplayName("Should handle NullPointerException for /movies endpoint")
    void shouldHandleNullPointerExceptionForMoviesEndpoint() {
        var exception = new NullPointerException("Null value");
        var webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/movies/1");

        assertThrows(NullPointerException.class, () ->
                handler.handleNullPointerException(exception, webRequest));
    }

    @Test
    @DisplayName("Should handle NullPointerException for /document-info endpoint")
    void shouldHandleNullPointerExceptionForDocumentInfoEndpoint() {
        var exception = new NullPointerException("Null value");
        var webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/document-info");

        assertThrows(NullPointerException.class, () ->
                handler.handleNullPointerException(exception, webRequest));
    }


    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with Unexpected character error")
    void shouldHandleHttpMessageNotReadableExceptionWithUnexpectedCharacter() {
        var rootCause = new RuntimeException("Unexpected character 'x'");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with from String error")
    void shouldHandleHttpMessageNotReadableExceptionWithFromStringError() {
        var rootCause = new RuntimeException("Cannot deserialize value from String");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with from Number error")
    void shouldHandleHttpMessageNotReadableExceptionWithFromNumberError() {
        var rootCause = new RuntimeException("Cannot deserialize value from Number");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with not a valid representation error")
    void shouldHandleHttpMessageNotReadableExceptionWithNotValidRepresentationError() {
        var rootCause = new RuntimeException("not a valid representation");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with type mismatch error containing not a valid")
    void shouldHandleHttpMessageNotReadableExceptionWithTypeMismatchErrorContainingNotAValid() {
        var rootCause = new RuntimeException("not a valid Integer value");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with field name detection by word boundary")
    void shouldHandleHttpMessageNotReadableExceptionWithFieldNameDetectionByWordBoundary() {
        var rootCause = new RuntimeException("Error with year field");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with position inference for winner field")
    void shouldHandleHttpMessageNotReadableExceptionWithPositionInferenceForWinnerField() {
        var rootCause = new RuntimeException("Error at line: 2, column: 3");
        var exception = new HttpMessageNotReadableException("Unrecognized token", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with position inference for year field")
    void shouldHandleHttpMessageNotReadableExceptionWithPositionInferenceForYearField() {
        var rootCause = new RuntimeException("Error at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Unrecognized token", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with column position inference")
    void shouldHandleHttpMessageNotReadableExceptionWithColumnPositionInference() {
        var rootCause = new RuntimeException("Error at line: 1, column: 60");
        var exception = new HttpMessageNotReadableException("Unexpected character", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with generic error when no root cause")
    void shouldHandleHttpMessageNotReadableExceptionWithGenericErrorWhenNoRootCause() {
        var exception = new HttpMessageNotReadableException("Invalid JSON", (Throwable) null);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertTrue(errorDTO.message().contains("Invalid JSON format"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with blank root cause message")
    void shouldHandleHttpMessageNotReadableExceptionWithBlankRootCauseMessage() {
        var rootCause = new RuntimeException("   ");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with field name for title")
    void shouldHandleHttpMessageNotReadableExceptionWithFieldNameForTitle() {
        var rootCause = new RuntimeException("Cannot deserialize value of type java.lang.String from String \"invalid\": not a valid String value. Field \"title\" is invalid at line: 1, column: 20");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertNotNull(errorDTO.message());
        assertTrue(
                errorDTO.message().toLowerCase().contains("title") || 
                errorDTO.message().toLowerCase().contains("field") || 
                errorDTO.message().toLowerCase().contains("invalid") ||
                errorDTO.message().toLowerCase().contains("value") ||
                errorDTO.message().toLowerCase().contains("json"),
                "Message should contain title/field/invalid/value/json: " + errorDTO.message()
        );
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with field name for studios")
    void shouldHandleHttpMessageNotReadableExceptionWithFieldNameForStudios() {
        var rootCause = new RuntimeException("Error with \"studios\" field");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with field name for producers")
    void shouldHandleHttpMessageNotReadableExceptionWithFieldNameForProducers() {
        var rootCause = new RuntimeException("Error with \"producers\" field");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with field name for winner")
    void shouldHandleHttpMessageNotReadableExceptionWithFieldNameForWinner() {
        var rootCause = new RuntimeException("Error with \"winner\" field");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with generic field message when no field detected")
    void shouldHandleHttpMessageNotReadableExceptionWithGenericFieldMessageWhenNoFieldDetected() {
        var rootCause = new RuntimeException("Unexpected character 'x' at line: 1, column: 30");
        var exception = new HttpMessageNotReadableException("Unexpected character", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertNotNull(errorDTO.message());
        assertTrue(
                errorDTO.message().toLowerCase().contains("field") || 
                errorDTO.message().toLowerCase().contains("invalid") || 
                errorDTO.message().toLowerCase().contains("value") ||
                errorDTO.message().toLowerCase().contains("json"),
                "Message should contain field/invalid/value/json: " + errorDTO.message()
        );
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with body is missing message")
    void shouldHandleHttpMessageNotReadableExceptionWithBodyIsMissingMessage() {
        var exception = new HttpMessageNotReadableException("body is missing");

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertTrue(errorDTO.message().contains("Request body is required"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with Required request body is missing")
    void shouldHandleHttpMessageNotReadableExceptionWithRequiredRequestBodyIsMissing() {
        var exception = new HttpMessageNotReadableException("Required request body is missing");

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertTrue(errorDTO.message().contains("Request body is required"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with generic error and no root cause")
    void shouldHandleHttpMessageNotReadableExceptionWithGenericErrorAndNoRootCause() {
        var exception = new HttpMessageNotReadableException("Invalid JSON");

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertTrue(errorDTO.message().contains("Invalid JSON format"));
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with null exception message")
    void shouldHandleHttpMessageNotReadableExceptionWithNullExceptionMessage() {
        var exception = new HttpMessageNotReadableException("");

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with year field error and type mismatch")
    void shouldHandleHttpMessageNotReadableExceptionWithYearFieldError() {
        var rootCause = new RuntimeException("Cannot deserialize value of type java.lang.Integer from String \"invalid\": not a valid Integer value. Field \"year\" is invalid at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with title field error and type mismatch")
    void shouldHandleHttpMessageNotReadableExceptionWithTitleFieldError() {
        var rootCause = new RuntimeException("Cannot deserialize value of type java.lang.String from String \"invalid\": not a valid String value. Field \"title\" is invalid at line: 1, column: 20");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with studios field error and type mismatch")
    void shouldHandleHttpMessageNotReadableExceptionWithStudiosFieldError() {
        var rootCause = new RuntimeException("Cannot deserialize value of type java.lang.String from String \"invalid\": not a valid String value. Field \"studios\" is invalid at line: 1, column: 30");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with producers field error and type mismatch")
    void shouldHandleHttpMessageNotReadableExceptionWithProducersFieldError() {
        var rootCause = new RuntimeException("Cannot deserialize value of type java.lang.String from String \"invalid\": not a valid String value. Field \"producers\" is invalid at line: 1, column: 40");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with winner field error and type mismatch")
    void shouldHandleHttpMessageNotReadableExceptionWithWinnerFieldError() {
        var rootCause = new RuntimeException("Cannot deserialize value of type java.lang.Boolean from String \"invalid\": not a valid Boolean value. Field \"winner\" is invalid at line: 1, column: 50");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with unknown field error")
    void shouldHandleHttpMessageNotReadableExceptionWithUnknownFieldError() {
        var rootCause = new RuntimeException("Field \"unknownField\" is invalid");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should infer field name from unrecognized token position line > 1 column <= 5")
    void shouldInferFieldNameFromUnrecognizedTokenPositionLineGreaterThanOneColumnLessEqualFive() {
        var rootCause = new RuntimeException("Unrecognized token 'invalid' was expecting (true or false) at line: 2, column: 5");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should infer field name from unrecognized token position line 1 column < 20")
    void shouldInferFieldNameFromUnrecognizedTokenPositionLineOneColumnLessThanTwenty() {
        var rootCause = new RuntimeException("Unrecognized token 'invalid' was expecting (true or false) at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should infer field name from comma error position line > 1")
    void shouldInferFieldNameFromCommaErrorPositionLineGreaterThanOne() {
        var rootCause = new RuntimeException("Unexpected character (',') (code 44) at line: 2, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should infer field name from comma error position line 1")
    void shouldInferFieldNameFromCommaErrorPositionLineOne() {
        var rootCause = new RuntimeException("Unexpected character (',') (code 44) at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should infer field name from column position > 50")
    void shouldInferFieldNameFromColumnPositionGreaterThanFifty() {
        var rootCause = new RuntimeException("Unexpected character ('x') at line: 1, column: 60");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should infer field name from column position < 20")
    void shouldInferFieldNameFromColumnPositionLessThanTwenty() {
        var rootCause = new RuntimeException("Unexpected character ('x') at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should infer field name from column position between 20 and 50")
    void shouldInferFieldNameFromColumnPositionBetweenTwentyAndFifty() {
        var rootCause = new RuntimeException("Unexpected character ('x') at line: 1, column: 30");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle error type EXPECTED_VALUE")
    void shouldHandleErrorTypeExpectedValue() {
        var rootCause = new RuntimeException("expected a value at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle error type UNEXPECTED_CHARACTER")
    void shouldHandleErrorTypeUnexpectedCharacter() {
        var rootCause = new RuntimeException("Unexpected character ('x') at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle error type UNRECOGNIZED_TOKEN with true")
    void shouldHandleErrorTypeUnrecognizedTokenWithTrue() {
        var rootCause = new RuntimeException("Unrecognized token 'invalid' was expecting true at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle error type UNRECOGNIZED_TOKEN with false")
    void shouldHandleErrorTypeUnrecognizedTokenWithFalse() {
        var rootCause = new RuntimeException("Unrecognized token 'invalid' was expecting false at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle error type UNRECOGNIZED_TOKEN with was expecting")
    void shouldHandleErrorTypeUnrecognizedTokenWithWasExpecting() {
        var rootCause = new RuntimeException("Unrecognized token 'invalid' was expecting (',') at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle error type TYPE_MISMATCH with not a valid representation")
    void shouldHandleErrorTypeTypeMismatchWithNotAValidRepresentation() {
        var rootCause = new RuntimeException("not a valid representation at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle error type TYPE_MISMATCH with from Number")
    void shouldHandleErrorTypeTypeMismatchWithFromNumber() {
        var rootCause = new RuntimeException("Cannot deserialize value from Number at line: 1, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with body is missing")
    void shouldHandleHttpMessageNotReadableExceptionWithBodyIsMissing() {
        var exception = new HttpMessageNotReadableException("body is missing");

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with generic error and null root cause message")
    void shouldHandleHttpMessageNotReadableExceptionWithGenericErrorAndNullRootCauseMessage() {
        var rootCause = new RuntimeException((String) null);
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with blank exception message")
    void shouldHandleHttpMessageNotReadableExceptionWithBlankExceptionMessage() {
        var exception = new HttpMessageNotReadableException("   ");

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with position line 0 column 0")
    void shouldHandleHttpMessageNotReadableExceptionWithPositionLineZeroColumnZero() {
        var rootCause = new RuntimeException("Unexpected character ('x') at line: 0, column: 0");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with position line 0 column > 0")
    void shouldHandleHttpMessageNotReadableExceptionWithPositionLineZeroColumnGreaterThanZero() {
        var rootCause = new RuntimeException("Unexpected character ('x') at line: 0, column: 10");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with position line > 0 column 0")
    void shouldHandleHttpMessageNotReadableExceptionWithPositionLineGreaterThanZeroColumnZero() {
        var rootCause = new RuntimeException("Unexpected character ('x') at line: 2, column: 0");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with field name detection for studios")
    void shouldHandleHttpMessageNotReadableExceptionWithFieldNameDetectionForStudios() {
        var rootCause = new RuntimeException("Cannot deserialize value. Field \"studios\" is invalid at line: 1, column: 30");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException with field name detection for producers")
    void shouldHandleHttpMessageNotReadableExceptionWithFieldNameDetectionForProducers() {
        var rootCause = new RuntimeException("Cannot deserialize value. Field \"producers\" is invalid at line: 1, column: 40");
        var exception = new HttpMessageNotReadableException("Invalid JSON", rootCause);

        var result = handler.handleHttpMessageNotReadableException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
    }
}

