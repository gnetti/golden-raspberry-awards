package golden.raspberry.awards.core.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Producer Value Object Tests")
class ProducerTest {

    @Test
    @DisplayName("Should create producer with valid name")
    void shouldCreateProducerWithValidName() {
        var producer = new Producer("John Doe");

        assertEquals("John Doe", producer.name());
    }

    @Test
    @DisplayName("Should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new Producer(null));

        assertEquals("Producer name cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new Producer("   "));

        assertEquals("Producer name cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should create producer using factory method")
    void shouldCreateProducerUsingFactoryMethod() {
        var producer = Producer.of("John Doe");

        assertEquals("John Doe", producer.name());
    }

    @Test
    @DisplayName("Should trim name in factory method")
    void shouldTrimNameInFactoryMethod() {
        var producer = Producer.of("  John Doe  ");

        assertEquals("John Doe", producer.name());
    }

    @Test
    @DisplayName("Should parse single producer")
    void shouldParseSingleProducer() {
        var producers = Producer.parseMultiple("John Doe");

        assertEquals(1, producers.size());
        assertEquals("John Doe", producers.get(0).name());
    }

    @Test
    @DisplayName("Should parse multiple producers separated by comma")
    void shouldParseMultipleProducersSeparatedByComma() {
        var producers = Producer.parseMultiple("John Doe, Jane Smith");

        assertEquals(2, producers.size());
        assertEquals("John Doe", producers.get(0).name());
        assertEquals("Jane Smith", producers.get(1).name());
    }

    @Test
    @DisplayName("Should parse multiple producers separated by and")
    void shouldParseMultipleProducersSeparatedByAnd() {
        var producers = Producer.parseMultiple("John Doe and Jane Smith");

        assertEquals(2, producers.size());
        assertEquals("John Doe", producers.get(0).name());
        assertEquals("Jane Smith", producers.get(1).name());
    }

    @Test
    @DisplayName("Should parse multiple producers with mixed separators")
    void shouldParseMultipleProducersWithMixedSeparators() {
        var producers = Producer.parseMultiple("John Doe, Jane Smith and Bob Wilson");

        assertEquals(3, producers.size());
        assertEquals("John Doe", producers.get(0).name());
        assertEquals("Jane Smith", producers.get(1).name());
        assertEquals("Bob Wilson", producers.get(2).name());
    }

    @Test
    @DisplayName("Should return empty list when input is null")
    void shouldReturnEmptyListWhenInputIsNull() {
        var producers = Producer.parseMultiple(null);

        assertTrue(producers.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when input is blank")
    void shouldReturnEmptyListWhenInputIsBlank() {
        var producers = Producer.parseMultiple("   ");

        assertTrue(producers.isEmpty());
    }

    @Test
    @DisplayName("Should trim producer names when parsing")
    void shouldTrimProducerNamesWhenParsing() {
        var producers = Producer.parseMultiple("  John Doe  ,  Jane Smith  ");

        assertEquals(2, producers.size());
        assertEquals("John Doe", producers.get(0).name());
        assertEquals("Jane Smith", producers.get(1).name());
    }

    @Test
    @DisplayName("Should filter out blank producer names")
    void shouldFilterOutBlankProducerNames() {
        var producers = Producer.parseMultiple("John Doe, , Jane Smith");

        assertEquals(2, producers.size());
        assertEquals("John Doe", producers.get(0).name());
        assertEquals("Jane Smith", producers.get(1).name());
    }

    @Test
    @DisplayName("Should return name in toString")
    void shouldReturnNameInToString() {
        var producer = new Producer("John Doe");

        assertEquals("John Doe", producer.toString());
    }
}

