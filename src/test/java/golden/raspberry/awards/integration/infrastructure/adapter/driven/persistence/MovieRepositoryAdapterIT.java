package golden.raspberry.awards.integration.infrastructure.adapter.driven.persistence;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.integration.helper.IntegrationTestBase;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.adapter.MovieRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration-test")
class MovieRepositoryAdapterIT extends IntegrationTestBase {

    @Autowired
    private MovieRepositoryAdapter repositoryAdapter;

    @BeforeEach
    @Override
    protected void ensureSchemaReady() {
        super.ensureSchemaReady();
        safeCleanup();
    }

    @Test
    void shouldSaveMovieWithId() {
        var movie = new Movie(2020, "Test Movie", "Test Studio", "Test Producer", true);
        var saved = repositoryAdapter.saveWithId(movie, 10000L);

        assertNotNull(saved);
        assertEquals(10000L, saved.id());
        assertEquals("Test Movie", saved.title());
    }

    @Test
    void shouldFindMovieById() {
        var movie = new Movie(2020, "Test Movie", "Test Studio", "Test Producer", true);
        repositoryAdapter.saveWithId(movie, 10000L);

        var found = repositoryAdapter.findByIdWithId(10000L);

        assertTrue(found.isPresent());
        assertEquals(10000L, found.get().id());
        assertEquals("Test Movie", found.get().title());
    }

    @Test
    void shouldReturnEmptyWhenMovieNotFound() {
        var found = repositoryAdapter.findByIdWithId(99999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteMovieById() {
        var movie = new Movie(2020, "Test Movie", "Test Studio", "Test Producer", true);
        repositoryAdapter.saveWithId(movie, 10000L);

        var deleted = repositoryAdapter.deleteById(10000L);

        assertTrue(deleted);
        assertTrue(repositoryAdapter.findByIdWithId(10000L).isEmpty());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentMovie() {
        var deleted = repositoryAdapter.deleteById(99999L);
        assertFalse(deleted);
    }

    @Test
    void shouldFindAllMoviesWithPagination() {
        var initialCount = jpaRepository.count();
        repositoryAdapter.saveWithId(new Movie(2020, "Movie 1", "Studio 1", "Producer 1", true), 10000L);
        repositoryAdapter.saveWithId(new Movie(2021, "Movie 2", "Studio 2", "Producer 2", false), 10001L);
        repositoryAdapter.saveWithId(new Movie(2022, "Movie 3", "Studio 3", "Producer 3", true), 10002L);

        assertTrue(repositoryAdapter.findByIdWithId(10000L).isPresent(), "Movie 1 should be saved");
        assertTrue(repositoryAdapter.findByIdWithId(10001L).isPresent(), "Movie 2 should be saved");
        assertTrue(repositoryAdapter.findByIdWithId(10002L).isPresent(), "Movie 3 should be saved");

        var pageable = PageRequest.of(0, 2, Sort.by("id"));
        var page = repositoryAdapter.findAll(pageable);

        assertTrue(page.getTotalElements() >= initialCount + 3, 
                "Total should include at least the 3 new movies plus initial data from CsvDataLoader");
        assertTrue(page.getContent().size() >= 2, "Page should contain at least 2 movies");
        assertEquals(2, page.getSize(), "Page size should be 2");
    }

    @Test
    void shouldFindMoviesWithFilterByTitle() {
        repositoryAdapter.saveWithId(new Movie(2020, "UniqueTestMovie123", "Studio 1", "Producer 1", true), 10000L);
        repositoryAdapter.saveWithId(new Movie(2021, "Another Movie", "Studio 2", "Producer 2", false), 10001L);

        var pageable = PageRequest.of(0, 10);
        var page = repositoryAdapter.findAllWithFilter("title", "UniqueTestMovie123", pageable);

        assertTrue(page.getTotalElements() >= 1);
        assertTrue(page.getContent().stream().anyMatch(m -> m.title().equals("UniqueTestMovie123")));
    }

    @Test
    void shouldFindMoviesWithFilterByYear() {
        repositoryAdapter.saveWithId(new Movie(2099, "Movie 1", "Studio 1", "Producer 1", true), 10000L);
        repositoryAdapter.saveWithId(new Movie(2098, "Movie 2", "Studio 2", "Producer 2", false), 10001L);

        var pageable = PageRequest.of(0, 10);
        var page = repositoryAdapter.findAllWithFilter("year", "2099", pageable);

        assertTrue(page.getTotalElements() >= 1);
        assertTrue(page.getContent().stream().anyMatch(m -> m.year() == 2099));
        assertTrue(page.getContent().stream().anyMatch(m -> m.id() == 10000L),
                "Should find the movie with year 2099 that was just created");
    }

    @Test
    void shouldFindMoviesWithFilterByStudios() {
        repositoryAdapter.saveWithId(new Movie(2020, "Movie 1", "UniqueTestStudio123", "Producer 1", true), 10000L);
        repositoryAdapter.saveWithId(new Movie(2021, "Movie 2", "Another Studio", "Producer 2", false), 10001L);

        var pageable = PageRequest.of(0, 10);
        var page = repositoryAdapter.findAllWithFilter("studios", "UniqueTestStudio123", pageable);

        assertTrue(page.getTotalElements() >= 1);
        assertTrue(page.getContent().stream().anyMatch(m -> m.studios().equals("UniqueTestStudio123")));
    }

    @Test
    void shouldFindMoviesWithFilterByProducers() {
        repositoryAdapter.saveWithId(new Movie(2020, "Movie 1", "Studio 1", "UniqueTestProducer123", true), 10000L);
        repositoryAdapter.saveWithId(new Movie(2021, "Movie 2", "Studio 2", "Another Producer", false), 10001L);

        var pageable = PageRequest.of(0, 10);
        var page = repositoryAdapter.findAllWithFilter("producers", "UniqueTestProducer123", pageable);

        assertTrue(page.getTotalElements() >= 1);
        assertTrue(page.getContent().stream().anyMatch(m -> m.producers().equals("UniqueTestProducer123")));
    }

    @Test
    void shouldFindMoviesWithFilterByAllFields() {
        repositoryAdapter.saveWithId(new Movie(2020, "UniqueTestMovie456", "Studio 1", "Producer 1", true), 10000L);
        repositoryAdapter.saveWithId(new Movie(2021, "Another Movie", "Studio 2", "Producer 2", false), 10001L);

        var pageable = PageRequest.of(0, 10);
        var page = repositoryAdapter.findAllWithFilter("all", "UniqueTestMovie456", pageable);

        assertTrue(page.getTotalElements() >= 1);
        assertTrue(page.getContent().stream().anyMatch(m -> m.title().equals("UniqueTestMovie456")));
    }

    @Test
    void shouldReturnAllMoviesWhenFilterIsBlank() {
        repositoryAdapter.saveWithId(new Movie(2020, "Movie 1", "Studio 1", "Producer 1", true), 10000L);
        repositoryAdapter.saveWithId(new Movie(2021, "Movie 2", "Studio 2", "Producer 2", false), 10001L);

        assertTrue(repositoryAdapter.findByIdWithId(10000L).isPresent(), "Movie 1 should be saved");
        assertTrue(repositoryAdapter.findByIdWithId(10001L).isPresent(), "Movie 2 should be saved");

        var totalCount = jpaRepository.count();
        assertTrue(totalCount >= 2, "Should have at least the 2 newly created movies");

        var pageable = PageRequest.of(0, Math.max(10, (int) totalCount));
        var page = repositoryAdapter.findAllWithFilter("all", "", pageable);

        assertEquals(totalCount, page.getTotalElements(),
                "Page total should match the total count from repository when filter is blank");
        assertTrue(page.getContent().stream().anyMatch(m -> m.id() == 10000L || m.id() == 10001L),
                "Should contain at least one of the newly created movies");
    }

    @Test
    void shouldFindByWinnerTrue() {
        var initialWinners = repositoryAdapter.findByWinnerTrue().size();
        repositoryAdapter.saveWithId(new Movie(2020, "UniqueWinnerMovie789", "Studio 1", "Producer 1", true), 10000L);
        repositoryAdapter.saveWithId(new Movie(2021, "Loser Movie", "Studio 2", "Producer 2", false), 10001L);

        var winners = repositoryAdapter.findByWinnerTrue();

        assertTrue(winners.size() >= initialWinners + 1);
        assertTrue(winners.stream().anyMatch(m -> m.title().equals("UniqueWinnerMovie789")));
    }

    @Test
    void shouldRequireManualIdAssignment() {
        var movie1 = new Movie(2020, "Movie 1", "Studio 1", "Producer 1", true);
        var movie2 = new Movie(2021, "Movie 2", "Studio 2", "Producer 2", false);

        var saved1 = repositoryAdapter.saveWithId(movie1, 20000L);
        var saved2 = repositoryAdapter.saveWithId(movie2, 20001L);

        assertEquals(20000L, saved1.id(), 
                "MovieEntity does NOT have @GeneratedValue. ID must be provided manually. " +
                "IDs come from CSV (when loading) or XML (when creating via API)");
        assertEquals(20001L, saved2.id(), 
                "Each movie must have a unique ID provided manually. " +
                "The entity does not auto-generate IDs");

        var found1 = repositoryAdapter.findByIdWithId(20000L);
        var found2 = repositoryAdapter.findByIdWithId(20001L);

        assertTrue(found1.isPresent(), "Movie with manually assigned ID 20000 should exist");
        assertTrue(found2.isPresent(), "Movie with manually assigned ID 20001 should exist");
        assertEquals(20000L, found1.get().id());
        assertEquals(20001L, found2.get().id());
    }
}

