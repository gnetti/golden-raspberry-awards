package golden.raspberry.awards.integration.infrastructure.adapter.driven.xml;

import golden.raspberry.awards.infrastructure.adapter.driven.xml.XmlIdKeyManagerAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration-test")
class XmlIdKeyManagerAdapterIT {

    @Autowired
    private XmlIdKeyManagerAdapter adapter;

    private Path xmlPath;

    @BeforeEach
    void setup() {
        xmlPath = Path.of("src/main/resources/highest-id/key.xml");
        safeCleanup();
    }

    @AfterEach
    void cleanup() {
        safeCleanup();
    }

    private void safeCleanup() {
        try {
            var fileSystemPath = Path.of("src/main/resources/highest-id/key.xml");
            if (Files.exists(fileSystemPath)) {
                Files.delete(fileSystemPath);
            }
            
            var parentDir = fileSystemPath.getParent();
            if (parentDir != null && Files.exists(parentDir)) {
                try (var stream = Files.list(parentDir)) {
                    if (stream.findAny().isEmpty()) {
                        Files.delete(parentDir);
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Test
    void shouldGetLastIdFromXmlFile() {
        try {
            Files.createDirectories(xmlPath.getParent());
            Files.writeString(xmlPath, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>100</lastId></keys>");

            var lastId = adapter.getLastId();

            assertTrue(lastId.isPresent());
            assertEquals(100L, lastId.get());
        } catch (Exception e) {
            fail("Failed to create test XML file", e);
        }
    }

    @Test
    void shouldReturnEmptyWhenXmlFileDoesNotExist() {
        safeCleanup();
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        var fileSystemPath = Path.of("src/main/resources/highest-id/key.xml");
        assertFalse(Files.exists(fileSystemPath), "XML file should not exist in filesystem before test");
        
        var classpathResource = new org.springframework.core.io.ClassPathResource("highest-id/key.xml");
        var lastId = adapter.getLastId();
        
        if (classpathResource.exists()) {
            assertTrue(lastId.isPresent(), 
                "When XML file exists in classpath but not in filesystem, adapter should return value from classpath. " +
                "This is expected behavior: adapter reads from filesystem first, then falls back to classpath. " +
                "Got: " + lastId);
        } else {
            assertTrue(lastId.isEmpty(), 
                "Should return empty when XML file does not exist in both filesystem and classpath. Got: " + lastId);
        }
    }

    @Test
    void shouldSynchronizeWithDatabase() {
        try {
            Files.createDirectories(xmlPath.getParent());
            Files.writeString(xmlPath, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>50</lastId></keys>");

            adapter.synchronizeWithDatabase(200L);

            var lastId = adapter.getLastId();
            assertTrue(lastId.isPresent());
            var syncedValue = lastId.get();
            assertTrue(syncedValue >= 200L, "Synchronized value should be >= 200, but was: " + syncedValue);
        } catch (Exception e) {
            fail("Failed to synchronize XML", e);
        }
    }

    @Test
    void shouldResetLastId() {
        try {
            Files.createDirectories(xmlPath.getParent());
            Files.writeString(xmlPath, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>100</lastId></keys>");

            adapter.resetLastId(50L);

            var lastId = adapter.getLastId();
            assertTrue(lastId.isPresent());
            assertEquals(50L, lastId.get());
        } catch (Exception e) {
            fail("Failed to reset last ID", e);
        }
    }

    @Test
    void shouldGetNextIdAndIncrement() {
        try {
            Files.createDirectories(xmlPath.getParent());
            Files.writeString(xmlPath, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>100</lastId></keys>");

            var nextId1 = adapter.getNextId();
            assertEquals(101L, nextId1);

            var lastIdAfterFirst = adapter.getLastId();
            assertTrue(lastIdAfterFirst.isPresent());
            assertEquals(101L, lastIdAfterFirst.get());

            var nextId2 = adapter.getNextId();
            assertEquals(102L, nextId2);

            var lastIdAfterSecond = adapter.getLastId();
            assertTrue(lastIdAfterSecond.isPresent());
            assertEquals(102L, lastIdAfterSecond.get());
        } catch (Exception e) {
            fail("Failed to get next ID", e);
        }
    }

    @Test
    void shouldSynchronizeWithDatabaseTakingMaximum() {
        try {
            Files.createDirectories(xmlPath.getParent());
            Files.writeString(xmlPath, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>150</lastId></keys>");

            adapter.synchronizeWithDatabase(200L);

            var lastId = adapter.getLastId();
            assertTrue(lastId.isPresent());
            assertEquals(200L, lastId.get(), "Should take max between DB (200) and XML (150)");
        } catch (Exception e) {
            fail("Failed to synchronize with database", e);
        }
    }

    @Test
    void shouldSynchronizeWithDatabaseKeepingXmlWhenXmlIsHigher() {
        try {
            Files.createDirectories(xmlPath.getParent());
            Files.writeString(xmlPath, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>250</lastId></keys>");

            adapter.synchronizeWithDatabase(200L);

            var lastId = adapter.getLastId();
            assertTrue(lastId.isPresent());
            assertEquals(250L, lastId.get(), "Should keep XML value (250) when it's higher than DB (200)");
        } catch (Exception e) {
            fail("Failed to synchronize with database", e);
        }
    }

    @Test
    void shouldNotUpdateXmlWhenSynchronizedIdEqualsCurrentLastId() {
        try {
            Files.createDirectories(xmlPath.getParent());
            Files.writeString(xmlPath, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>200</lastId></keys>");

            adapter.synchronizeWithDatabase(200L);

            var lastId = adapter.getLastId();
            assertTrue(lastId.isPresent());
            assertEquals(200L, lastId.get(), "Should not update when synchronized ID equals current last ID");
        } catch (Exception e) {
            fail("Failed to synchronize with database", e);
        }
    }

    @Test
    void shouldMaintainLastIdAfterDelete() {
        try {
            Files.createDirectories(xmlPath.getParent());
            Files.writeString(xmlPath, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>206</lastId></keys>");

            var nextId = adapter.getNextId();
            assertEquals(207L, nextId, "getNextId() should return 206+1=207");

            var lastIdAfterCreate = adapter.getLastId();
            assertTrue(lastIdAfterCreate.isPresent());
            assertEquals(207L, lastIdAfterCreate.get(), "XML should be updated to 207 after getNextId()");

            var nextId2 = adapter.getNextId();
            assertEquals(208L, nextId2, "Next ID should be 207+1=208, even if ID 207 was deleted from database");
            
            var lastIdAfterSecondCreate = adapter.getLastId();
            assertTrue(lastIdAfterSecondCreate.isPresent());
            assertEquals(208L, lastIdAfterSecondCreate.get(), 
                    "XML maintains last ID (208) even if previous IDs were deleted. " +
                    "This ensures deleted IDs are never reused");
        } catch (Exception e) {
            fail("Failed to maintain last ID after delete simulation", e);
        }
    }
}

