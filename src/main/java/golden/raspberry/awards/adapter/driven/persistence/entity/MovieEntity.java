package golden.raspberry.awards.adapter.driven.persistence.entity;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * JPA Entity for Movie.
 * Represents the database table structure.
 * Schema is created automatically via JPA (ddl-auto=create-drop).
 * <p>
 * Uses Java 21 features: Records (if applicable), clean code.
 */
@Entity
@Table(name = "movies")
public class MovieEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NonNull
    private Integer year;
    
    @Column(nullable = false, length = 500)
    @NonNull
    private String title;
    
    @Column(nullable = false, length = 500)
    @NonNull
    private String studios;
    
    @Column(nullable = false, length = 1000)
    @NonNull
    private String producers;
    
    @Column(nullable = false)
    @NonNull
    private Boolean winner;

    public MovieEntity() {
    }

    public MovieEntity(@NonNull Integer year, @NonNull String title, 
                      @NonNull String studios, @NonNull String producers, 
                      @NonNull Boolean winner) {
        this.year = Objects.requireNonNull(year, "Year cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.studios = Objects.requireNonNull(studios, "Studios cannot be null");
        this.producers = Objects.requireNonNull(producers, "Producers cannot be null");
        this.winner = winner;
    }

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    @NonNull
    public Integer getYear() { 
        return year; 
    }
    
    public void setYear(@NonNull Integer year) { 
        this.year = Objects.requireNonNull(year, "Year cannot be null");
    }
    
    @NonNull
    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(@NonNull String title) { 
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }
    
    @NonNull
    public String getStudios() { 
        return studios; 
    }
    
    public void setStudios(@NonNull String studios) { 
        this.studios = Objects.requireNonNull(studios, "Studios cannot be null");
    }
    
    @NonNull
    public String getProducers() { 
        return producers; 
    }
    
    public void setProducers(@NonNull String producers) { 
        this.producers = Objects.requireNonNull(producers, "Producers cannot be null");
    }
    
    @NonNull
    public Boolean getWinner() { 
        return winner; 
    }
    
    public void setWinner(@NonNull Boolean winner) { 
        this.winner = winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieEntity that = (MovieEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    @NonNull
    public String toString() {
        return "MovieEntity{id=%d, year=%d, title='%s', winner=%s}"
                .formatted(id, year, title, winner);
    }
}

