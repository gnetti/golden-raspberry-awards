package golden.raspberry.awards.core.domain.model.valueobject;

import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Value Object representing a Producer.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record Producer(String name) {
    public Producer {
        Objects.requireNonNull(name, "Producer name cannot be null");
        if (name.trim().isBlank()) {
            throw new IllegalArgumentException("Producer name cannot be blank");
        }
    }

    public static Producer of(String name) {
        return new Producer(name.trim());
    }

    /**
     * Parses a producer string that may contain multiple producers separated by comma or "and".
     *
     * @param producersString String containing one or more producers
     * @return List of Producer objects parsed from the string
     */
    public static List<Producer> parseMultiple(String producersString) {
        if (producersString == null || producersString.isBlank()) {
            return List.of();
        }

        return Arrays.stream(producersString.split("(,| and )"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Producer::of)
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public String toString() {
        return name;
    }
}
