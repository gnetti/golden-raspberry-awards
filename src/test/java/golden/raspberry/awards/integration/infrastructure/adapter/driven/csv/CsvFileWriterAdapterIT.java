package golden.raspberry.awards.integration.infrastructure.adapter.driven.csv;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import golden.raspberry.awards.integration.helper.IntegrationTestBase;
import golden.raspberry.awards.infrastructure.adapter.driven.csv.CsvFileWriterAdapter;
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
class CsvFileWriterAdapterIT extends IntegrationTestBase {

    @Autowired
    private CsvFileWriterAdapter csvWriterAdapter;

    private Path testCsvPath;

    @BeforeEach
    @Override
    protected void ensureSchemaReady() {
        super.ensureSchemaReady();
        testCsvPath = Path.of("src/main/resources/data/movieList.csv");
    }

    @Test
    void shouldAppendMovieToCsv() {
        var movie = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        assertDoesNotThrow(() -> csvWriterAdapter.appendMovie(movie));

        assertTrue(Files.exists(testCsvPath));
    }

    @Test
    void shouldUpdateMovieInCsv() {
        var movie1 = new MovieWithId(1L, 2020, "Original Movie", "Studio 1", "Producer 1", true);
        var movie2 = new MovieWithId(1L, 2021, "Updated Movie", "Studio 2", "Producer 2", false);

        assertDoesNotThrow(() -> {
            csvWriterAdapter.appendMovie(movie1);
            csvWriterAdapter.updateMovie(movie2);
        });

        assertTrue(Files.exists(testCsvPath));
    }

    @Test
    void shouldRemoveMovieFromCsv() {
        var movie = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        assertDoesNotThrow(() -> {
            csvWriterAdapter.appendMovie(movie);
            csvWriterAdapter.removeMovie(1L);
        });

        assertTrue(Files.exists(testCsvPath));
    }
}

