package golden.raspberry.awards.infrastructure.adapter.driven.listener.recording;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResultRecorderAdapter Tests")
class ResultRecorderAdapterTest {

    private ResultRecorderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ResultRecorderAdapter();
    }

    @Test
    @DisplayName("Should record result successfully")
    void shouldRecordResultSuccessfully() {
        var result = new Object();

        assertDoesNotThrow(() -> adapter.record(result));
    }

    @Test
    @DisplayName("Should throw exception when record result is null")
    void shouldThrowExceptionWhenRecordResultIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.record(null));

        assertEquals("Result cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should store result successfully")
    void shouldStoreResultSuccessfully() {
        var result = new Object();

        assertDoesNotThrow(() -> adapter.store(result));
    }

    @Test
    @DisplayName("Should throw exception when store result is null")
    void shouldThrowExceptionWhenStoreResultIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.store(null));

        assertEquals("Result cannot be null", exception.getMessage());
    }
}

