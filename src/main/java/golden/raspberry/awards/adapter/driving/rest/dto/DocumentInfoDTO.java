package golden.raspberry.awards.adapter.driving.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for documentation information page.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Schema(description = "Documentation information data")
public record DocumentInfoDTO(
        @Schema(description = "API version", example = "1.0.0")
        String apiVersion,

        @Schema(description = "Base URL", example = "http://localhost:8080")
        String baseUrl,

        @Schema(description = "API description", example = "RestFul API for managing Golden Raspberry Awards movie data")
        String description,

        @Schema(description = "Architecture style", example = "Hexagonal Architecture (Ports & Adapters)")
        String architecture,

        @Schema(description = "Maturity level", example = "Richardson Level 2")
        String maturityLevel,

        @Schema(description = "Author name", example = "Luiz Generoso")
        String author
) {
    /**
     * Creates a DocumentInfoDTO with default values.
     *
     * @return DocumentInfoDTO with default values
     */
    public static DocumentInfoDTO createDefault() {
        return new DocumentInfoDTO(
                "1.0.0",
                "http://localhost:8080",
                "RestFul API for managing Golden Raspberry Awards movie data",
                "Hexagonal Architecture (Ports & Adapters)",
                "Richardson Level 2",
                "Luiz Generoso"
        );
    }
}

