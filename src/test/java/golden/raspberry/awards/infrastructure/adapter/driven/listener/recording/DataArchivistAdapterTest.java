package golden.raspberry.awards.infrastructure.adapter.driven.listener.recording;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataArchivistAdapter Tests")
class DataArchivistAdapterTest {

    private DataArchivistAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new DataArchivistAdapter();
    }

    @Test
    @DisplayName("Should archive data successfully")
    void shouldArchiveDataSuccessfully() {
        var data = new Object();

        assertDoesNotThrow(() -> adapter.archive(data));
    }

    @Test
    @DisplayName("Should throw exception when archive data is null")
    void shouldThrowExceptionWhenArchiveDataIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.archive(null));

        assertEquals("Data cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should preserve data successfully")
    void shouldPreserveDataSuccessfully() {
        var data = new Object();

        assertDoesNotThrow(() -> adapter.preserve(data));
    }

    @Test
    @DisplayName("Should throw exception when preserve data is null")
    void shouldThrowExceptionWhenPreserveDataIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.preserve(null));

        assertEquals("Data cannot be null", exception.getMessage());
    }
}

