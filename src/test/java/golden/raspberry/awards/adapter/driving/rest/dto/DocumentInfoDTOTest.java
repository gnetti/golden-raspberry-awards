package golden.raspberry.awards.adapter.driving.rest.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DocumentInfoDTO Tests")
class DocumentInfoDTOTest {

    @Test
    @DisplayName("Should create DocumentInfoDTO with default values")
    void shouldCreateDocumentInfoDTOWithDefaultValues() {
        var result = DocumentInfoDTO.createDefault();

        assertNotNull(result);
        assertEquals("1.0.0", result.apiVersion());
        assertEquals("http://localhost:8080", result.baseUrl());
        assertEquals("RestFul API for managing Golden Raspberry Awards movie data", result.description());
        assertEquals("Hexagonal Architecture (Ports & Adapters)", result.architecture());
        assertEquals("Richardson Level 2", result.maturityLevel());
        assertEquals("Luiz Generoso", result.author());
    }

    @Test
    @DisplayName("Should create DocumentInfoDTO with custom values")
    void shouldCreateDocumentInfoDTOWithCustomValues() {
        var result = new DocumentInfoDTO(
                "2.0.0",
                "http://example.com",
                "Custom description",
                "Custom architecture",
                "Level 3",
                "Custom Author"
        );

        assertEquals("2.0.0", result.apiVersion());
        assertEquals("http://example.com", result.baseUrl());
        assertEquals("Custom description", result.description());
        assertEquals("Custom architecture", result.architecture());
        assertEquals("Level 3", result.maturityLevel());
        assertEquals("Custom Author", result.author());
    }
}

