package golden.raspberry.awards.integration.adapter.driving.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import golden.raspberry.awards.adapter.driving.rest.dto.CreateMovieDTO;
import golden.raspberry.awards.integration.helper.IntegrationTestBase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration-test")
class AwardsControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @Override
    protected void ensureSchemaReady() {
        super.ensureSchemaReady();
        safeCleanup();
    }

    private void setupTestData() throws Exception {
        var producer1 = "Test Producer A";
        var producer2 = "Test Producer B";
        
        createMovie(2000, "Movie 1", "Studio 1", producer1, true);
        createMovie(2005, "Movie 2", "Studio 2", producer1, true);
        createMovie(2010, "Movie 3", "Studio 3", producer2, true);
        createMovie(2020, "Movie 4", "Studio 4", producer2, true);
    }

    private void createMovie(int year, String title, String studio, String producer, boolean winner) throws Exception {
        var createDTO = new CreateMovieDTO(year, title, studio, producer, winner);
        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());
    }

    /**
     * Tests that the intervals endpoint returns a successful response
     * with the expected structure (min and max arrays).
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldReturnIntervals() throws Exception {
        setupTestData();
        mockMvc.perform(get("/api/movies/producers/intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min").exists())
                .andExpect(jsonPath("$.max").exists())
                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.max").isArray());
    }

    @Test
    void shouldReturnValidIntervalStructure() throws Exception {
        setupTestData();
        mockMvc.perform(get("/api/movies/producers/intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.max").isArray())
                .andExpect(jsonPath("$.min[0].producer").exists())
                .andExpect(jsonPath("$.min[0].interval").exists())
                .andExpect(jsonPath("$.min[0].previousWin").exists())
                .andExpect(jsonPath("$.min[0].followingWin").exists())
                .andExpect(jsonPath("$.max[0].producer").exists())
                .andExpect(jsonPath("$.max[0].interval").exists())
                .andExpect(jsonPath("$.max[0].previousWin").exists())
                .andExpect(jsonPath("$.max[0].followingWin").exists());
    }

    @Test
    void shouldReturnValidDataTypes() throws Exception {
        setupTestData();
        mockMvc.perform(get("/api/movies/producers/intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min[0].producer").isString())
                .andExpect(jsonPath("$.min[0].interval").isNumber())
                .andExpect(jsonPath("$.min[0].previousWin").isNumber())
                .andExpect(jsonPath("$.min[0].followingWin").isNumber())
                .andExpect(jsonPath("$.max[0].producer").isString())
                .andExpect(jsonPath("$.max[0].interval").isNumber())
                .andExpect(jsonPath("$.max[0].previousWin").isNumber())
                .andExpect(jsonPath("$.max[0].followingWin").isNumber());
    }

    @Test
    void shouldReturnValidIntervalValues() throws Exception {
        setupTestData();
        mockMvc.perform(get("/api/movies/producers/intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min[0].interval").value(Matchers.greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.max[0].interval").value(Matchers.greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.min[0].previousWin").isNumber())
                .andExpect(jsonPath("$.min[0].followingWin").isNumber())
                .andExpect(jsonPath("$.max[0].previousWin").isNumber())
                .andExpect(jsonPath("$.max[0].followingWin").isNumber());
    }

    /**
     * Tests that the intervals endpoint returns content type JSON.
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldReturnJsonContentType() throws Exception {
        setupTestData();
        mockMvc.perform(get("/api/movies/producers/intervals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}

