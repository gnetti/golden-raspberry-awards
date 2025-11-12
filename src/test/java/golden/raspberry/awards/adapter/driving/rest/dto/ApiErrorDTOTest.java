package golden.raspberry.awards.adapter.driving.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiErrorDTO Tests")
class ApiErrorDTOTest {

    @Test
    @DisplayName("Should create ApiErrorDTO with factory method")
    void shouldCreateApiErrorDTOWithFactoryMethod() {
        var result = ApiErrorDTO.of(400, "Bad Request", "Invalid input", "/api/movies");

        assertNotNull(result);
        assertEquals(400, result.status());
        assertEquals("Bad Request", result.error());
        assertEquals("Invalid input", result.message());
        assertEquals("/api/movies", result.path());
        assertNotNull(result.timestamp());
    }

    @Test
    @DisplayName("Should throw exception when timestamp is null")
    void shouldThrowExceptionWhenTimestampIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ApiErrorDTO(null, 400, "Error", "Message", "/path"));

        assertEquals("Timestamp cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when status is null")
    void shouldThrowExceptionWhenStatusIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ApiErrorDTO(LocalDateTime.now(), null, "Error", "Message", "/path"));

        assertEquals("Status cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when error is null")
    void shouldThrowExceptionWhenErrorIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ApiErrorDTO(LocalDateTime.now(), 400, null, "Message", "/path"));

        assertEquals("Error cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when error is blank")
    void shouldThrowExceptionWhenErrorIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new ApiErrorDTO(LocalDateTime.now(), 400, "   ", "Message", "/path"));

        assertEquals("Error cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when message is null")
    void shouldThrowExceptionWhenMessageIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ApiErrorDTO(LocalDateTime.now(), 400, "Error", null, "/path"));

        assertEquals("Message cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when message is blank")
    void shouldThrowExceptionWhenMessageIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new ApiErrorDTO(LocalDateTime.now(), 400, "Error", "   ", "/path"));

        assertEquals("Message cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when path is null")
    void shouldThrowExceptionWhenPathIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ApiErrorDTO(LocalDateTime.now(), 400, "Error", "Message", null));

        assertEquals("Path cannot be null", exception.getMessage());
    }
}

