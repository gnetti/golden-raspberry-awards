package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Movie in REST API. *
 * <p>Follows Richardson Level 2: structured response format.
 * Includes ID for update/delete operations.
 * *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record MovieDTO(
        @JsonProperty("id")
        Long id,

        @JsonProperty("year")
        Integer year,

        @JsonProperty("title")
        String title,

        @JsonProperty("studios")
        String studios,

        @JsonProperty("producers")
        String producers,

        @JsonProperty("winner")
        Boolean winner
) {
    /**
     * Compact constructor.
     * MovieDTO is a response DTO created from validated domain models.
     * No validation needed as it's created from already validated data.
     * @param id        Movie ID
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Whether the movie is a winner
     */
    public MovieDTO {
    }

}
