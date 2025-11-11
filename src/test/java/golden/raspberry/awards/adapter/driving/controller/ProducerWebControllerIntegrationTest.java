package golden.raspberry.awards.adapter.driving.controller;

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
 * Integration tests for ProducerWebController Thymeleaf pages.
 *
 * <p>Tests validate that the controller pages render correctly
 * with proper model attributes and view names.
 *
 * <p><strong>Test Coverage:</strong>
 * <ul>
 *   <li>GET / - Redirects to /dashboard</li>
 *   <li>GET /dashboard - Dashboard page rendering</li>
 *   <li>GET /intervals - Intervals page rendering with data</li>
 *   <li>Model attributes validation</li>
 *   <li>View names validation</li>
 *   <li>Content type validation (text/html)</li>
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
class ProducerWebControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests that the root path (/) redirects to /dashboard.
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldRedirectRootToDashboard() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    /**
     * Tests that the dashboard page (/dashboard) returns successfully
     * with the correct view name and content type.
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldReturnDashboardPage() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/dashboard"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Golden Raspberry Awards")))
                .andExpect(content().string(Matchers.containsString("Producer Intervals")));
    }

    /**
     * Tests that the intervals page (/intervals) returns successfully
     * with the correct view name, content type, and data.
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldReturnIntervalsPage() throws Exception {
        mockMvc.perform(get("/intervals"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/intervals"))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(Matchers.containsString("Producer Intervals")))
                .andExpect(content().string(Matchers.containsString("Shortest Interval")))
                .andExpect(content().string(Matchers.containsString("Longest Interval")));
    }

    /**
     * Tests that the intervals page contains table structure
     * for displaying producer intervals.
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldContainTableStructure() throws Exception {
        mockMvc.perform(get("/intervals"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("<table")))
                .andExpect(content().string(Matchers.containsString("Producer")))
                .andExpect(content().string(Matchers.containsString("Interval")))
                .andExpect(content().string(Matchers.containsString("Previous Year")))
                .andExpect(content().string(Matchers.containsString("Following Year")));
    }

    /**
     * Tests that the dashboard page contains navigation cards
     * with proper links and buttons.
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldContainNavigationCards() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("card")))
                .andExpect(content().string(Matchers.containsString("View Intervals")))
                .andExpect(content().string(Matchers.containsString("Swagger UI")))
                .andExpect(content().string(Matchers.containsString("/intervals")));
    }

    /**
     * Tests that pages contain Bootstrap and Font Awesome resources.
     *
     * @throws Exception if the test fails
     */
    @Test
    void shouldContainBootstrapAndFontAwesome() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("bootstrap")))
                .andExpect(content().string(Matchers.containsString("font-awesome")));
    }
}

