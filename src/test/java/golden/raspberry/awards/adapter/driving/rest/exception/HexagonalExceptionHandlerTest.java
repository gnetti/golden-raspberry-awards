package golden.raspberry.awards.adapter.driving.rest.exception;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.shared.exception.ApplicationException;
import golden.raspberry.awards.shared.exception.InfrastructureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("HexagonalExceptionHandler Tests")
class HexagonalExceptionHandlerTest {

    private HexagonalExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new HexagonalExceptionHandler();
        var httpServletRequest = mock(HttpServletRequest.class);
        webRequest = new ServletWebRequest(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/movies");
    }

    @Test
    @DisplayName("Should handle ApplicationException")
    void shouldHandleApplicationException() {
        var exception = new ApplicationException("Application error");

        var result = handler.handleApplicationException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(400, errorDTO.status());
        assertEquals("Application Error", errorDTO.error());
        assertEquals("Application error", errorDTO.message());
    }

    @Test
    @DisplayName("Should throw exception when ApplicationException is null")
    void shouldThrowExceptionWhenApplicationExceptionIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                handler.handleApplicationException(null, webRequest));

        assertEquals("ApplicationException cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when WebRequest is null for ApplicationException")
    void shouldThrowExceptionWhenWebRequestIsNullForApplicationException() {
        var exception = new ApplicationException("Error");

        var npe = assertThrows(NullPointerException.class, () ->
                handler.handleApplicationException(exception, null));

        assertEquals("WebRequest cannot be null", npe.getMessage());
    }

    @Test
    @DisplayName("Should handle InfrastructureException")
    void shouldHandleInfrastructureException() {
        var exception = new InfrastructureException("Infrastructure error");

        var result = handler.handleInfrastructureException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(500, errorDTO.status());
        assertEquals("Infrastructure Error", errorDTO.error());
        assertEquals("Infrastructure error", errorDTO.message());
    }

    @Test
    @DisplayName("Should throw exception when InfrastructureException is null")
    void shouldThrowExceptionWhenInfrastructureExceptionIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                handler.handleInfrastructureException(null, webRequest));

        assertEquals("InfrastructureException cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when WebRequest is null for InfrastructureException")
    void shouldThrowExceptionWhenWebRequestIsNullForInfrastructureException() {
        var exception = new InfrastructureException("Error");

        var npe = assertThrows(NullPointerException.class, () ->
                handler.handleInfrastructureException(exception, null));

        assertEquals("WebRequest cannot be null", npe.getMessage());
    }
}

