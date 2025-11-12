package golden.raspberry.awards.infrastructure.adapter.driven.listener.observation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChangeDetectorAdapter Tests")
class ChangeDetectorAdapterTest {

    private ChangeDetectorAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ChangeDetectorAdapter();
    }

    @Test
    @DisplayName("Should detect changes successfully")
    void shouldDetectChangesSuccessfully() {
        var before = new Object();
        var after = new Object();

        var result = adapter.detect(before, after);

        assertNull(result);
    }

    @Test
    @DisplayName("Should throw exception when before state is null")
    void shouldThrowExceptionWhenBeforeStateIsNull() {
        var after = new Object();

        var exception = assertThrows(NullPointerException.class, () ->
                adapter.detect(null, after));

        assertEquals("Before state cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when after state is null")
    void shouldThrowExceptionWhenAfterStateIsNull() {
        var before = new Object();

        var exception = assertThrows(NullPointerException.class, () ->
                adapter.detect(before, null));

        assertEquals("After state cannot be null", exception.getMessage());
    }
}

