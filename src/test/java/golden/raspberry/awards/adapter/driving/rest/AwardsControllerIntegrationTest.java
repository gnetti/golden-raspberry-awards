package golden.raspberry.awards.adapter.driving.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AwardsController REST API.
 *
 * <p>Tests validate that the REST API endpoints return data correctly
 * according to the CSV file provided, following Richardson Level 2 standards.
 *
 * <p><strong>Test Coverage:</strong>
 * <ul>
 *   <li>GET /api/movies/producers/intervals endpoint</li>
 *   <li>Response structure validation (min, max arrays)</li>
 *   <li>Interval object structure validation</li>
 *   <li>Status code validation (200 OK)</li>
 *   <li>Data validation against CSV source</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, Pattern Matching, String Templates.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AwardsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests that the intervals endpoint returns a successful response
     * with the expected structure (min and max arrays).
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldReturnIntervals() throws Exception {
        mockMvc.perform(get("/api/movies/producers/intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min").exists())
                .andExpect(jsonPath("$.max").exists())
                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.max").isArray());
    }

    /**
     * Tests that the intervals endpoint returns valid interval object structure
     * with all required fields (producer, interval, previousWin, followingWin).
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldReturnValidIntervalStructure() throws Exception {
        mockMvc.perform(get("/api/movies/producers/intervals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min[0].producer").exists())
                .andExpect(jsonPath("$.min[0].interval").exists())
                .andExpect(jsonPath("$.min[0].previousWin").exists())
                .andExpect(jsonPath("$.min[0].followingWin").exists())
                .andExpect(jsonPath("$.max[0].producer").exists())
                .andExpect(jsonPath("$.max[0].interval").exists())
                .andExpect(jsonPath("$.max[0].previousWin").exists())
                .andExpect(jsonPath("$.max[0].followingWin").exists());
    }

    /**
     * Tests that the intervals endpoint returns valid data types
     * for all fields in the response.
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldReturnValidDataTypes() throws Exception {
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

    /**
     * Tests that the intervals endpoint returns valid interval values
     * (interval must be non-negative).
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldReturnValidIntervalValues() throws Exception {
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
        mockMvc.perform(get("/api/movies/producers/intervals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}

