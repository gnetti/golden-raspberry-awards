package golden.raspberry.awards.infrastructure.adapter.driven.listener.transmission;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InformationEmitterAdapter Tests")
class InformationEmitterAdapterTest {

    private InformationEmitterAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new InformationEmitterAdapter();
    }

    @Test
    @DisplayName("Should emit information with session successfully")
    void shouldEmitInformationWithSessionSuccessfully() {
        var information = new Object();
        var sessionId = "session-123";

        assertDoesNotThrow(() -> adapter.withSession(information, sessionId));
    }

    @Test
    @DisplayName("Should throw exception when information is null")
    void shouldThrowExceptionWhenInformationIsNull() {
        var sessionId = "session-123";

        var exception = assertThrows(NullPointerException.class, () ->
                adapter.withSession(null, sessionId));

        assertEquals("Information cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when sessionId is null")
    void shouldThrowExceptionWhenSessionIdIsNull() {
        var information = new Object();

        var exception = assertThrows(NullPointerException.class, () ->
                adapter.withSession(information, null));

        assertEquals("SessionId cannot be null", exception.getMessage());
    }
}

