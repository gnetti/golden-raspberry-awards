package golden.raspberry.awards.integration.infrastructure.adapter.driven.file;

import golden.raspberry.awards.infrastructure.adapter.driven.file.FileListenerAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "listener.enabled=true")
@DirtiesContext
@Tag("integration-test")
class FileListenerAdapterIT {

    @Autowired
    private FileListenerAdapter fileListenerAdapter;

    private Path basePath;

    @BeforeEach
    void setup() {
        basePath = Path.of("src/main/resources/listener");
    }

    @AfterEach
    void cleanup() {
        try {
            if (Files.exists(basePath)) {
                try (Stream<Path> paths = Files.walk(basePath)) {
                    paths.sorted((a, b) -> -a.compareTo(b))
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (Exception ignored) {
                                }
                            });
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    void shouldWriteGetOperationToFile() {
        fileListenerAdapter.listenGet("session1", "GET", "/api/movies/1", 200,
                "Movie", "1", "test data", null);

        var today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        var folderPath = basePath.resolve(today);
        assertTrue(Files.exists(folderPath), "Folder should exist: " + folderPath);
        
        try (Stream<Path> files = Files.list(folderPath)) {
            var logFile = files.filter(p -> p.getFileName().toString().startsWith("listener_session1_"))
                    .findFirst();
            assertTrue(logFile.isPresent(), "Log file should exist for session1");
        } catch (Exception e) {
            fail("Failed to check log files", e);
        }
    }

    @Test
    void shouldWritePostOperationToFile() {
        fileListenerAdapter.listenPost("session2", "POST", "/api/movies", 201,
                "Movie", "2", "request data", "response data", null);

        var today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        var folderPath = basePath.resolve(today);
        assertTrue(Files.exists(folderPath), "Folder should exist: " + folderPath);
        
        try (Stream<Path> files = Files.list(folderPath)) {
            var logFile = files.filter(p -> p.getFileName().toString().startsWith("listener_session2_"))
                    .findFirst();
            assertTrue(logFile.isPresent(), "Log file should exist for session2");
        } catch (Exception e) {
            fail("Failed to check log files", e);
        }
    }

    @Test
    void shouldWritePutOperationToFile() {
        fileListenerAdapter.listenPut("session3", "PUT", "/api/movies/3", 200,
                "Movie", "3", "before data", "after data", null);

        var today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        var folderPath = basePath.resolve(today);
        assertTrue(Files.exists(folderPath), "Folder should exist: " + folderPath);
        
        try (Stream<Path> files = Files.list(folderPath)) {
            var logFile = files.filter(p -> p.getFileName().toString().startsWith("listener_session3_"))
                    .findFirst();
            assertTrue(logFile.isPresent(), "Log file should exist for session3");
        } catch (Exception e) {
            fail("Failed to check log files", e);
        }
    }

    @Test
    void shouldWriteDeleteOperationToFile() {
        fileListenerAdapter.listenDelete("session4", "DELETE", "/api/movies/4", 204,
                "Movie", "4", "deleted data", null);

        var today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        var folderPath = basePath.resolve(today);
        assertTrue(Files.exists(folderPath), "Folder should exist: " + folderPath);
        
        try (Stream<Path> files = Files.list(folderPath)) {
            var logFile = files.filter(p -> p.getFileName().toString().startsWith("listener_session4_"))
                    .findFirst();
            assertTrue(logFile.isPresent(), "Log file should exist for session4");
        } catch (Exception e) {
            fail("Failed to check log files", e);
        }
    }

    @Test
    void shouldWriteErrorToFile() {
        fileListenerAdapter.listenGet("session5", "GET", "/api/movies/999", 404,
                "Movie", "999", null, "Not found");

        var today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        var folderPath = basePath.resolve(today);
        assertTrue(Files.exists(folderPath), "Folder should exist: " + folderPath);
        
        try (Stream<Path> files = Files.list(folderPath)) {
            var logFile = files.filter(p -> p.getFileName().toString().startsWith("listener_session5_"))
                    .findFirst();
            assertTrue(logFile.isPresent(), "Log file should exist for session5");
        } catch (Exception e) {
            fail("Failed to check log files", e);
        }
    }
}

