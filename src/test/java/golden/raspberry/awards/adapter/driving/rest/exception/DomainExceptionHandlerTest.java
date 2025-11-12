package golden.raspberry.awards.adapter.driving.rest.exception;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.shared.exception.DomainException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("DomainExceptionHandler Tests")
class DomainExceptionHandlerTest {

    private DomainExceptionHandler handler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        handler = new DomainExceptionHandler();
        var httpServletRequest = mock(HttpServletRequest.class);
        webRequest = new ServletWebRequest(httpServletRequest);
        when(httpServletRequest.getRequestURI()).thenReturn("/api/movies");
    }

    @Test
    @DisplayName("Should handle DomainException")
    void shouldHandleDomainException() {
        var exception = new DomainException("Domain validation error");

        var result = handler.handleDomainException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(400, errorDTO.status());
        assertEquals("Domain Validation Error", errorDTO.error());
        assertEquals("Domain validation error", errorDTO.message());
    }

    @Test
    @DisplayName("Should throw exception when DomainException is null")
    void shouldThrowExceptionWhenDomainExceptionIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                handler.handleDomainException(null, webRequest));

        assertEquals("DomainException cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when WebRequest is null")
    void shouldThrowExceptionWhenWebRequestIsNull() {
        var exception = new DomainException("Error");

        var npe = assertThrows(NullPointerException.class, () ->
                handler.handleDomainException(exception, null));

        assertEquals("WebRequest cannot be null", npe.getMessage());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException")
    void shouldHandleConstraintViolationException() {
        var constraintViolation = new SimpleConstraintViolation("Invalid value");
        var exception = new ConstraintViolationException(Set.of(constraintViolation));

        var result = handler.handleConstraintViolationException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals(400, errorDTO.status());
        assertEquals("Validation Error", errorDTO.error());
        assertEquals("Invalid value", errorDTO.message());
    }

    private static class SimpleConstraintViolation implements ConstraintViolation<Object> {
        private final String message;

        SimpleConstraintViolation(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return message;
        }

        @Override
        public Object getRootBean() {
            return null;
        }

        @Override
        public Class<Object> getRootBeanClass() {
            return Object.class;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public jakarta.validation.Path getPropertyPath() {
            return null;
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public jakarta.validation.metadata.ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> type) {
            return null;
        }
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with empty violations")
    void shouldHandleConstraintViolationExceptionWithEmptyViolations() {
        var exception = new ConstraintViolationException(Set.of());

        var result = handler.handleConstraintViolationException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
        assertEquals("Invalid request parameter", errorDTO.message());
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
        assertEquals("Validation Error", errorDTO.error());
        assertEquals("Invalid argument", errorDTO.message());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with null message")
    void shouldHandleIllegalArgumentExceptionWithNullMessage() {
        var exception = new IllegalArgumentException();

        var result = handler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        var errorDTO = result.getBody();
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
}

