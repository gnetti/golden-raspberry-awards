package golden.raspberry.awards.integration.adapter.driving.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import golden.raspberry.awards.adapter.driving.rest.dto.CreateMovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.UpdateMovieDTO;
import golden.raspberry.awards.integration.helper.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration-test")
class MovieControllerIT extends IntegrationTestBase {

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

    @Test
    void shouldCreateMovie() throws Exception {
        var createDTO = new CreateMovieDTO(2020, "Test Movie", "Test Studio", "Test Producer", true);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.year").value(2020))
                .andExpect(jsonPath("$.title").value("Test Movie"))
                .andExpect(jsonPath("$.studios").value("Test Studio"))
                .andExpect(jsonPath("$.producers").value("Test Producer"))
                .andExpect(jsonPath("$.winner").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingMovieWithInvalidData() throws Exception {
        var createDTO = new CreateMovieDTO(null, "", null, null, null);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllMoviesWithPagination() throws Exception {
        mockMvc.perform(get("/api/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void shouldGetMovieById() throws Exception {
        var createDTO = new CreateMovieDTO(2020, "Test Movie", "Test Studio", "Test Producer", true);

        var result = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        var id = responseBody.get("id").asLong();

        mockMvc.perform(get("/api/movies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistentMovie() throws Exception {
        mockMvc.perform(get("/api/movies/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateMovie() throws Exception {
        var createDTO = new CreateMovieDTO(2020, "Test Movie", "Test Studio", "Test Producer", true);

        var result = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        var id = responseBody.get("id").asLong();

        var updateDTO = new UpdateMovieDTO(2021, "Updated Movie", "Updated Studio", "Updated Producer", false);

        mockMvc.perform(put("/api/movies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.year").value(2021))
                .andExpect(jsonPath("$.title").value("Updated Movie"))
                .andExpect(jsonPath("$.studios").value("Updated Studio"))
                .andExpect(jsonPath("$.producers").value("Updated Producer"))
                .andExpect(jsonPath("$.winner").value(false));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentMovie() throws Exception {
        var updateDTO = new UpdateMovieDTO(2021, "Updated Movie", "Updated Studio", "Updated Producer", false);

        mockMvc.perform(put("/api/movies/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteMovie() throws Exception {
        var createDTO = new CreateMovieDTO(2020, "Test Movie", "Test Studio", "Test Producer", true);

        var result = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = objectMapper.readTree(result.getResponse().getContentAsString());
        var id = responseBody.get("id").asLong();

        mockMvc.perform(delete("/api/movies/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentMovie() throws Exception {
        mockMvc.perform(delete("/api/movies/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNormalizeInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/movies")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void shouldUseXmlToGenerateNextIdWhenCreatingMovie() throws Exception {
        var createDTO1 = new CreateMovieDTO(2020, "Movie 1", "Studio 1", "Producer 1", true);
        var createDTO2 = new CreateMovieDTO(2021, "Movie 2", "Studio 2", "Producer 2", false);

        var result1 = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO1)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody1 = objectMapper.readTree(result1.getResponse().getContentAsString());
        var id1 = responseBody1.get("id").asLong();

        var result2 = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO2)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody2 = objectMapper.readTree(result2.getResponse().getContentAsString());
        var id2 = responseBody2.get("id").asLong();

        assertEquals(id1 + 1, id2, 
                "Second movie ID should be incremented from first movie ID. " +
                "XML stores last ID and getNextId() increments it");
    }

    @Test
    void shouldNotReuseDeletedMovieId() throws Exception {
        var createDTO1 = new CreateMovieDTO(2020, "Movie 1", "Studio 1", "Producer 1", true);
        var createDTO2 = new CreateMovieDTO(2021, "Movie 2", "Studio 2", "Producer 2", false);

        var result1 = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO1)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody1 = objectMapper.readTree(result1.getResponse().getContentAsString());
        var id1 = responseBody1.get("id").asLong();

        var result2 = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO2)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody2 = objectMapper.readTree(result2.getResponse().getContentAsString());
        var id2 = responseBody2.get("id").asLong();

        mockMvc.perform(delete("/api/movies/{id}", id1))
                .andExpect(status().isNoContent());

        var createDTO3 = new CreateMovieDTO(2022, "Movie 3", "Studio 3", "Producer 3", true);
        var result3 = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO3)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody3 = objectMapper.readTree(result3.getResponse().getContentAsString());
        var id3 = responseBody3.get("id").asLong();

        assertNotEquals(id1, id3, 
                "Deleted movie ID should not be reused. XML maintains last ID even after deletion");
        assertEquals(id2 + 1, id3, 
                "New movie ID should continue from last created ID, not reuse deleted ID");
    }
}

