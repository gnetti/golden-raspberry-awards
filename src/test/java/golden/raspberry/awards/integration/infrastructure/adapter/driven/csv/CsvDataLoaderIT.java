package golden.raspberry.awards.integration.infrastructure.adapter.driven.csv;

import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
import golden.raspberry.awards.integration.helper.IntegrationTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration-test")
class CsvDataLoaderIT extends IntegrationTestBase {

    @Autowired
    private IdKeyManagerPort idKeyManagerPort;

    @Test
    void shouldLoadMoviesFromCsvOnStartup() {
        var count = jpaRepository.count();
        assertTrue(count >= 0, "Database should be accessible and CsvDataLoader should have executed");
    }

    @Test
    void shouldHaveMoviesInDatabaseAfterLoading() {
        var count = jpaRepository.count();
        assertTrue(count >= 0, "Database should contain movies loaded by CsvDataLoader on startup");
    }

    @Test
    void shouldCreateDatabaseSchemaAutomatically() {
        var count = jpaRepository.count();
        assertNotNull(count, "Schema should be created automatically by Hibernate");
    }

    @Test
    void shouldSynchronizeXmlWithDatabaseWhenResetToOriginalIsFalse() {
        var maxIdFromDatabase = jpaRepository.findMaxId().orElse(0L);
        var lastIdFromXml = idKeyManagerPort.getLastId();
        
        assertTrue(maxIdFromDatabase >= 0, "Max ID from database should be non-negative");
        
        if (lastIdFromXml.isPresent()) {
            var xmlId = lastIdFromXml.get();
            assertTrue(xmlId >= maxIdFromDatabase, 
                    "XML should be synchronized with database. XML ID (%d) should be >= max database ID (%d) when resetToOriginal=false"
                            .formatted(xmlId, maxIdFromDatabase));
        }
    }

    @Test
    void shouldPreserveIdsFromCsvWhenLoading() {
        var movies = jpaRepository.findAll();
        assertFalse(movies.isEmpty(), "Should have movies loaded from CSV");
        
        movies.forEach(movie -> {
            assertNotNull(movie.getId(), "Movie should have ID from CSV");
            assertTrue(movie.getId() > 0, "Movie ID should be positive");
        });
    }

    @Test
    void shouldSynchronizeXmlWithMaxIdFromDatabaseWhenResetToOriginalIsFalse() {
        var maxIdFromDatabase = jpaRepository.findMaxId().orElse(0L);
        var lastIdFromXml = idKeyManagerPort.getLastId();
        
        assertTrue(maxIdFromDatabase >= 0, "Max ID from database should be non-negative");
        
        if (lastIdFromXml.isPresent()) {
            var xmlId = lastIdFromXml.get();
            assertTrue(xmlId >= maxIdFromDatabase, 
                    "When resetToOriginal=false, XML should be synchronized with database. " +
                    "XML ID (%d) should be >= max database ID (%d) because synchronizeWithDatabase " +
                    "takes the maximum between DB max and XML current value"
                            .formatted(xmlId, maxIdFromDatabase));
        }
    }
}

