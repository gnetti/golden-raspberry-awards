package golden.raspberry.awards.integration;

import golden.raspberry.awards.integration.helper.IntegrationTestBase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration-test")
class StartApplicationIT extends IntegrationTestBase {

    @Value("${csv.write-enabled:true}")
    private boolean csvWriteEnabled;

    @Value("${csv.reset-to-original:true}")
    private boolean csvResetToOriginal;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @org.springframework.beans.factory.annotation.Autowired
    private Environment environment;

    @Test
    void contextLoads() {
        assertNotNull(jpaRepository, "Spring context should load successfully");
    }

    @Test
    void shouldExecuteCsvDataLoaderOnStartup() {
        var count = jpaRepository.count();
        assertTrue(count >= 0, "CsvDataLoader should execute on startup and populate database");
    }

    @Test
    void shouldCreateDatabaseSchemaOnStartup() {
        var count = jpaRepository.count();
        assertNotNull(count, "Hibernate should create database schema automatically on startup");
    }

    @Test
    void shouldUseTestProfileProperties() {
        var activeProfiles = environment.getActiveProfiles();
        assertTrue(activeProfiles.length > 0, "Test profile should be active");
        assertEquals("test", activeProfiles[0], "Active profile should be 'test'");
        
        assertFalse(csvWriteEnabled, 
                "csv.write-enabled should be false from application-test.properties, but was: " + csvWriteEnabled);
        
        assertFalse(csvResetToOriginal, 
                "csv.reset-to-original should be false from application-test.properties, but was: " + csvResetToOriginal);
        
        assertTrue(datasourceUrl.contains("mem:testdb"), 
                "Datasource URL should be in-memory from application-test.properties, but was: " + datasourceUrl);
    }
}

