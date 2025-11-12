package golden.raspberry.awards.infrastructure.adapter.driven.listener.monitoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcessObserverAdapter Tests")
class ProcessObserverAdapterTest {

    private ProcessObserverAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProcessObserverAdapter();
    }

    @Test
    @DisplayName("Should observe process successfully")
    void shouldObserveProcessSuccessfully() {
        var process = new Object();

        assertDoesNotThrow(() -> adapter.observe(process));
    }

    @Test
    @DisplayName("Should throw exception when process is null")
    void shouldThrowExceptionWhenProcessIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.observe(null));

        assertEquals("Process cannot be null", exception.getMessage());
    }
}

