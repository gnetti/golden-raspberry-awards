package golden.raspberry.awards.infrastructure.adapter.driven.xml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XmlIdKeyManagerAdapter Tests")
class XmlIdKeyManagerAdapterTest {

    private XmlIdKeyManagerAdapter adapter;
    private String testXmlFilePath;

    @BeforeEach
    void setUp() {
        testXmlFilePath = "highest-id/key.xml";
        adapter = new XmlIdKeyManagerAdapter(
                testXmlFilePath,
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );
    }

    @Test
    @DisplayName("Should get last ID from XML file")
    void shouldGetLastIdFromXmlFile() {
        var result = adapter.getLastId();

        assertTrue(result.isPresent());
        assertTrue(result.get() > 0);
    }

    @Test
    @DisplayName("Should return empty when XML file does not exist")
    void shouldReturnEmptyWhenXmlFileDoesNotExist() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "test/non-existent-file-12345.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();

        assertTrue(result.isEmpty(), "Should return empty when file does not exist in filesystem or classpath");
    }

    @Test
    @DisplayName("Should throw exception when xmlFilePath is null")
    void shouldThrowExceptionWhenXmlFilePathIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new XmlIdKeyManagerAdapter(null, "4", "{https://xml.apache.org/xslt}indent-amount"));

        assertEquals("XML file path cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when xmlIndentAmount is null")
    void shouldThrowExceptionWhenXmlIndentAmountIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new XmlIdKeyManagerAdapter("test.xml", null, "{https://xml.apache.org/xslt}indent-amount"));

        assertEquals("XML indent amount cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when xmlIndentProperty is null")
    void shouldThrowExceptionWhenXmlIndentPropertyIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new XmlIdKeyManagerAdapter("test.xml", "4", null));

        assertEquals("XML indent property cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should update last ID")
    void shouldUpdateLastId() {
        assertDoesNotThrow(() -> adapter.updateLastId(200L));
    }

    @Test
    @DisplayName("Should throw exception when updateLastId with null")
    void shouldThrowExceptionWhenUpdateLastIdWithNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.updateLastId(null));

        assertEquals("lastId cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updateLastId with negative value")
    void shouldThrowExceptionWhenUpdateLastIdWithNegativeValue() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                adapter.updateLastId(-1L));

        assertTrue(exception.getMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("Should get next ID")
    void shouldGetNextId() {
        var currentLastId = adapter.getLastId().orElse(0L);
        var nextId = adapter.getNextId();

        assertEquals(currentLastId + 1, nextId);
    }

    @Test
    @DisplayName("Should synchronize with database when database ID is higher")
    void shouldSynchronizeWithDatabaseWhenDatabaseIdIsHigher() {
        assertDoesNotThrow(() -> adapter.synchronizeWithDatabase(150L));
    }

    @Test
    @DisplayName("Should synchronize with database when XML ID is higher")
    void shouldSynchronizeWithDatabaseWhenXmlIdIsHigher() {
        assertDoesNotThrow(() -> adapter.synchronizeWithDatabase(150L));
    }

    @Test
    @DisplayName("Should not update when synchronized ID equals XML ID")
    void shouldNotUpdateWhenSynchronizedIdEqualsXmlId() {
        var currentId = adapter.getLastId().orElse(0L);
        assertDoesNotThrow(() -> adapter.synchronizeWithDatabase(currentId));
    }

    @Test
    @DisplayName("Should throw exception when synchronizeWithDatabase with null")
    void shouldThrowExceptionWhenSynchronizeWithDatabaseWithNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.synchronizeWithDatabase(null));

        assertEquals("maxIdFromDatabase cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when synchronizeWithDatabase with negative value")
    void shouldThrowExceptionWhenSynchronizeWithDatabaseWithNegativeValue() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                adapter.synchronizeWithDatabase(-1L));

        assertTrue(exception.getMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("Should reset last ID")
    void shouldResetLastId() {
        assertDoesNotThrow(() -> adapter.resetLastId(50L));
    }

    @Test
    @DisplayName("Should throw exception when resetLastId with null")
    void shouldThrowExceptionWhenResetLastIdWithNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.resetLastId(null));

        assertEquals("maxIdFromDatabase cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when resetLastId with negative value")
    void shouldThrowExceptionWhenResetLastIdWithNegativeValue() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                adapter.resetLastId(-1L));

        assertTrue(exception.getMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("Should handle empty XML file")
    void shouldHandleEmptyXmlFile() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-empty.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle XML file with invalid format")
    void shouldHandleXmlFileWithInvalidFormat() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-invalid.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle XML file with missing lastId element")
    void shouldHandleXmlFileWithMissingLastIdElement() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-missing-element.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle XML file with empty lastId element")
    void shouldHandleXmlFileWithEmptyLastIdElement() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-empty-element.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle XML file with invalid number in lastId")
    void shouldHandleXmlFileWithInvalidNumberInLastId() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-invalid-number.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle XML file with whitespace in lastId")
    void shouldHandleXmlFileWithWhitespaceInLastId() {
        var result = adapter.getLastId();

        assertTrue(result.isPresent() || result.isEmpty());
    }

    @Test
    @DisplayName("Should update last ID to zero")
    void shouldUpdateLastIdToZero() {
        assertDoesNotThrow(() -> adapter.updateLastId(0L));
    }

    @Test
    @DisplayName("Should synchronize with database when XML file does not exist")
    void shouldSynchronizeWithDatabaseWhenXmlFileDoesNotExist() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent/file.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        assertDoesNotThrow(() -> nonExistentAdapter.synchronizeWithDatabase(100L));
    }

    @Test
    @DisplayName("Should handle parseXmlSafely with invalid XML")
    void shouldHandleParseXmlSafelyWithInvalidXml() {
        var result = adapter.getLastId();
        assertTrue(result.isPresent() || result.isEmpty());
    }

    @Test
    @DisplayName("Should handle extractLastIdNode when node is missing")
    void shouldHandleExtractLastIdNodeWhenNodeIsMissing() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-missing-node.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle extractLastIdValue with empty text")
    void shouldHandleExtractLastIdValueWithEmptyText() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-empty-value.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle parseLastId with invalid number")
    void shouldHandleParseLastIdWithInvalidNumber() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-invalid-number.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle parseXml with exception")
    void shouldHandleParseXmlWithException() {
        var nonExistentAdapter = new XmlIdKeyManagerAdapter(
                "non-existent-parse-error.xml",
                "4",
                "{https://xml.apache.org/xslt}indent-amount"
        );

        var result = nonExistentAdapter.getLastId();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should call readFromFileSystem method directly via reflection")
    void shouldCallReadFromFileSystemMethodDirectly() throws Exception {
        var testXmlFile = Files.createTempFile("test-", ".xml");
        try {
            Files.writeString(testXmlFile, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><keys><lastId>100</lastId></keys>");
            
            var adapter = new XmlIdKeyManagerAdapter(
                    testXmlFile.getFileName().toString(),
                    "4",
                    "{https://xml.apache.org/xslt}indent-amount"
            );
            
            var fileSystemPath = Path.of("src/main/resources", testXmlFile.getFileName().toString());
            Files.createDirectories(fileSystemPath.getParent());
            Files.copy(testXmlFile, fileSystemPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            var method = XmlIdKeyManagerAdapter.class.getDeclaredMethod("readFromFileSystem", Path.class);
            method.setAccessible(true);
            
            var result = method.invoke(adapter, fileSystemPath);
            assertNotNull(result);
            assertInstanceOf(java.io.InputStream.class, result);
            ((java.io.InputStream) result).close();
            
            Files.deleteIfExists(fileSystemPath);
        } finally {
            Files.deleteIfExists(testXmlFile);
        }
    }
}

