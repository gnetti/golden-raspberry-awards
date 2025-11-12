package golden.raspberry.awards.infrastructure.adapter.driven.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import golden.raspberry.awards.core.application.service.ListenerAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FileListenerAdapter Tests")
class FileListenerAdapterTest {

    @TempDir
    Path tempDir;

    private ObjectMapper objectMapper;
    private ListenerAdapter listenerAdapter;
    private FileListenerAdapter adapter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        listenerAdapter = mock(ListenerAdapter.class);
        adapter = new FileListenerAdapter(
                objectMapper,
                listenerAdapter,
                true,
                tempDir.toString(),
                "listener",
                "yyyy-MM-dd",
                "yyyy-MM-dd_HH-mm-ss",
                7
        );
    }

    @Test
    @DisplayName("Should throw exception when ObjectMapper is null")
    void shouldThrowExceptionWhenObjectMapperIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new FileListenerAdapter(null, listenerAdapter, true, "path", "prefix", "format1", "format2", 7));

        assertEquals("ObjectMapper cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when ListenerAdapter is null")
    void shouldThrowExceptionWhenListenerAdapterIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new FileListenerAdapter(objectMapper, null, true, "path", "prefix", "format1", "format2", 7));

        assertEquals("ListenerAdapter cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when basePath is null")
    void shouldThrowExceptionWhenBasePathIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new FileListenerAdapter(objectMapper, listenerAdapter, true, null, "prefix", "format1", "format2", 7));

        assertEquals("Base path cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when prefix is null")
    void shouldThrowExceptionWhenPrefixIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new FileListenerAdapter(objectMapper, listenerAdapter, true, "path", null, "format1", "format2", 7));

        assertEquals("Prefix cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should normalize date format with hyphens")
    void shouldNormalizeDateFormatWithHyphens() {
        var adapterWithHyphens = new FileListenerAdapter(
                objectMapper,
                listenerAdapter,
                true,
                tempDir.toString(),
                "listener",
                "yyyy-MM-dd",
                "yyyy-MM-dd_HH-mm-ss",
                7
        );

        adapterWithHyphens.listenGet("session1", "GET", "/api/movies", 200, "Movie", "1", "data", null);

        verify(listenerAdapter).observeProcess(anyString());
    }

    @Test
    @DisplayName("Should handle parseFolderDate with invalid format")
    void shouldHandleParseFolderDateWithInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () ->
                new FileListenerAdapter(
                        objectMapper,
                        listenerAdapter,
                        true,
                        tempDir.toString(),
                        "listener",
                        "invalid-format",
                        "yyyy-MM-dd_HH-mm-ss",
                        7
                ));
    }

    @Test
    @DisplayName("Should handle IO error gracefully")
    void shouldHandleIoErrorGracefully() {
        var invalidPathAdapter = new FileListenerAdapter(
                objectMapper,
                listenerAdapter,
                true,
                tempDir.resolve("invalid").resolve("nested").toString(),
                "listener",
                "yyyy-MM-dd",
                "yyyy-MM-dd_HH-mm-ss",
                7
        );

        assertDoesNotThrow(() ->
                invalidPathAdapter.listenGet("session1", "GET", "/api/movies", 200, "Movie", "1", "data", null));
    }

    @Test
    @DisplayName("Should not execute when disabled")
    void shouldNotExecuteWhenDisabled() {
        var disabledAdapter = new FileListenerAdapter(
                objectMapper,
                listenerAdapter,
                false,
                tempDir.toString(),
                "listener",
                "yyyy-MM-dd",
                "yyyy-MM-dd HH:mm:ss",
                7
        );

        disabledAdapter.listenGet("session1", "GET", "/api/movies", 200, "Movie", "1", "data", null);

        verify(listenerAdapter, never()).observeProcess(anyString());
    }

    @Test
    @DisplayName("Should listen to GET operation")
    void shouldListenToGetOperation() {
        adapter.listenGet("session1", "GET", "/api/movies", 200, "Movie", "1", "data", null);

        verify(listenerAdapter).observeProcess(anyString());
        verify(listenerAdapter).emitWithSession(any(), eq("session1"));
        verify(listenerAdapter).recordResult(any());
    }

    @Test
    @DisplayName("Should listen to PUT operation")
    void shouldListenToPutOperation() {
        adapter.listenPut("session1", "PUT", "/api/movies/1", 200, "Movie", "1", "before", "after", null);

        verify(listenerAdapter).observeProcess(anyString());
        verify(listenerAdapter, times(2)).archiveData(any());
        verify(listenerAdapter).detectChanges(any(), any());
        verify(listenerAdapter).emitWithSession(any(), eq("session1"));
        verify(listenerAdapter).recordResult(any());
    }

    @Test
    @DisplayName("Should listen to DELETE operation")
    void shouldListenToDeleteOperation() {
        adapter.listenDelete("session1", "DELETE", "/api/movies/1", 200, "Movie", "1", "data", null);

        verify(listenerAdapter).observeProcess(anyString());
        verify(listenerAdapter).archiveData(any());
        verify(listenerAdapter).preserveData(any());
        verify(listenerAdapter).emitWithSession(any(), eq("session1"));
        verify(listenerAdapter).recordResult(any());
    }

    @Test
    @DisplayName("Should listen to POST operation")
    void shouldListenToPostOperation() {
        adapter.listenPost("session1", "POST", "/api/movies", 201, "Movie", "1", "request", "response", null);

        verify(listenerAdapter).observeProcess(anyString());
        verify(listenerAdapter, times(2)).emitWithSession(any(), eq("session1"));
        verify(listenerAdapter).storeData(any());
        verify(listenerAdapter).recordResult(any());
    }

    @Test
    @DisplayName("Should handle cleanup with retention days zero")
    void shouldHandleCleanupWithRetentionDaysZero() {
        var noRetentionAdapter = new FileListenerAdapter(
                objectMapper,
                listenerAdapter,
                true,
                tempDir.toString(),
                "listener",
                "yyyy-MM-dd",
                "yyyy-MM-dd_HH-mm-ss",
                0
        );

        assertDoesNotThrow(() ->
                noRetentionAdapter.listenGet("session1", "GET", "/api/movies", 200, "Movie", "1", "data", null));
    }

    @Test
    @DisplayName("Should handle cleanup with negative retention days")
    void shouldHandleCleanupWithNegativeRetentionDays() {
        var negativeRetentionAdapter = new FileListenerAdapter(
                objectMapper,
                listenerAdapter,
                true,
                tempDir.toString(),
                "listener",
                "yyyy-MM-dd",
                "yyyy-MM-dd_HH-mm-ss",
                -1
        );

        assertDoesNotThrow(() ->
                negativeRetentionAdapter.listenGet("session1", "GET", "/api/movies", 200, "Movie", "1", "data", null));
    }
}

