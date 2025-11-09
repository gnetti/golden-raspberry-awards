package golden.raspberry.awards.adapter.driven.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity for Movie.
 * Represents the database table structure.
 * Schema is created automatically via JPA (ddl-auto=create-drop).
 *
 * <p>All validations are performed in Driving Adapters (DTOs with Jakarta Validation) BEFORE reaching this entity.
 *
 * <p>Uses Java 21 features: String Templates for toString.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Entity
@Table(name = "movies")
public class MovieEntity {

    @Id
    private Long id;

    @Column(name = "\"year\"", nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String studios;

    @Column(nullable = false)
    private String producers;

    @Column(nullable = false)
    private Boolean winner;

    /**
     * Default constructor (required by JPA).
     */
    public MovieEntity() {
    }

    /**
     * Constructor with all fields.
     *
     * @param id        Movie ID
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Winner flag
     */
    public MovieEntity(Long id, Integer year, String title,
                       String studios, String producers, Boolean winner) {
        this.id = id;
        this.year = year;
        this.title = title;
        this.studios = studios;
        this.producers = producers;
        this.winner = winner;
    }

    /**
     * Constructor without ID (for new entities).
     *
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Winner flag
     */
    public MovieEntity(Integer year, String title,
                       String studios, String producers, Boolean winner) {
        this.year = year;
        this.title = title;
        this.studios = studios;
        this.producers = producers;
        this.winner = winner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudios() {
        return studios;
    }

    public void setStudios(String studios) {
        this.studios = studios;
    }

    public String getProducers() {
        return producers;
    }

    public void setProducers(String producers) {
        this.producers = producers;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieEntity that = (MovieEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MovieEntity{id=%d, year=%d, title='%s', winner=%s}"
                .formatted(id, year, title, winner);
    }
}
