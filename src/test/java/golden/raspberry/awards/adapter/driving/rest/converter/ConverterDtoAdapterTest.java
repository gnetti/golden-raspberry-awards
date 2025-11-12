package golden.raspberry.awards.adapter.driving.rest.converter;

import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerInterval;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerIntervalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConverterDtoAdapter Tests")
class ConverterDtoAdapterTest {

    private ConverterDtoAdapter converter;

    @BeforeEach
    void setUp() {
        converter = new ConverterDtoAdapter();
    }

    @Test
    @DisplayName("Should convert MovieWithId to MovieDTO")
    void shouldConvertMovieWithIdToMovieDTO() {
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        var result = converter.toDTO(movieWithId);

        assertInstanceOf(MovieDTO.class, result);
        var movieDTO = (MovieDTO) result;
        assertEquals(1L, movieDTO.id());
        assertEquals(2020, movieDTO.year());
        assertEquals("Test Movie", movieDTO.title());
        assertEquals("Test Studio", movieDTO.studios());
        assertEquals("Test Producer", movieDTO.producers());
        assertTrue(movieDTO.winner());
    }

    @Test
    @DisplayName("Should convert ProducerIntervalResponse to ProducerIntervalResponseDTO")
    void shouldConvertProducerIntervalResponseToDTO() {
        var interval1 = ProducerInterval.of("Producer 1", 2010, 2015);
        var interval2 = ProducerInterval.of("Producer 2", 2015, 2020);
        var response = new ProducerIntervalResponse(
                List.of(interval1),
                List.of(interval2)
        );

        var result = converter.toDTO(response);

        assertInstanceOf(ProducerIntervalResponseDTO.class, result);
        var dto = (ProducerIntervalResponseDTO) result;
        assertEquals(1, dto.min().size());
        assertEquals(1, dto.max().size());
        assertEquals("Producer 1", dto.min().get(0).producer());
        assertEquals("Producer 2", dto.max().get(0).producer());
    }

    @Test
    @DisplayName("Should convert ProducerInterval to ProducerIntervalDTO")
    void shouldConvertProducerIntervalToDTO() {
        var interval = ProducerInterval.of("Producer", 2010, 2015);

        var result = converter.toDTO(interval);

        assertInstanceOf(ProducerIntervalDTO.class, result);
        var dto = (ProducerIntervalDTO) result;
        assertEquals("Producer", dto.producer());
        assertEquals(5, dto.interval());
        assertEquals(2010, dto.previousWin());
        assertEquals(2015, dto.followingWin());
    }

    @Test
    @DisplayName("Should throw exception when domain model is null")
    void shouldThrowExceptionWhenDomainModelIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                converter.toDTO(null));

        assertEquals("Domain model cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when domain model type is unsupported")
    void shouldThrowExceptionWhenDomainModelTypeIsUnsupported() {
        var unsupportedModel = "Unsupported type";

        var exception = assertThrows(IllegalArgumentException.class, () ->
                converter.toDTO(unsupportedModel));

        assertTrue(exception.getMessage().contains("Unsupported domain model type"));
    }

    @Test
    @DisplayName("Should handle null ProducerIntervalResponse")
    void shouldHandleNullProducerIntervalResponse() {
        ProducerIntervalResponse nullResponse = null;

        var exception = assertThrows(NullPointerException.class, () ->
                converter.toDTO(nullResponse));

        assertEquals("Domain model cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle ProducerIntervalResponse with empty lists")
    void shouldHandleProducerIntervalResponseWithEmptyLists() {
        var response = new ProducerIntervalResponse(List.of(), List.of());

        var result = converter.toDTO(response);

        assertInstanceOf(ProducerIntervalResponseDTO.class, result);
        var dto = (ProducerIntervalResponseDTO) result;
        assertTrue(dto.min().isEmpty());
        assertTrue(dto.max().isEmpty());
    }

    @Test
    @DisplayName("Should handle ProducerIntervalResponse with valid intervals containing null values")
    void shouldHandleProducerIntervalResponseWithValidIntervals() {
        var interval1 = ProducerInterval.of("Producer 1", 2010, 2015);
        var response = new ProducerIntervalResponse(
                List.of(interval1),
                List.of()
        );

        var result = converter.toDTO(response);

        assertInstanceOf(ProducerIntervalResponseDTO.class, result);
        var dto = (ProducerIntervalResponseDTO) result;
        assertEquals(1, dto.min().size());
        assertTrue(dto.max().isEmpty());
    }

    @Test
    @DisplayName("Should call convertMovieWithId method directly")
    void shouldCallConvertMovieWithIdMethodDirectly() throws Exception {
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        Method method = ConverterDtoAdapter.class.getDeclaredMethod("convertMovieWithId", MovieWithId.class);
        method.setAccessible(true);

        var result = method.invoke(converter, movieWithId);

        assertInstanceOf(MovieDTO.class, result);
        var movieDTO = (MovieDTO) result;
        assertEquals(1L, movieDTO.id());
        assertEquals(2020, movieDTO.year());
    }

    @Test
    @DisplayName("Should call convertProducerIntervalResponse method directly")
    void shouldCallConvertProducerIntervalResponseMethodDirectly() throws Exception {
        var interval1 = ProducerInterval.of("Producer 1", 2010, 2015);
        var response = new ProducerIntervalResponse(List.of(interval1), List.of());
        Method method = ConverterDtoAdapter.class.getDeclaredMethod("convertProducerIntervalResponse", ProducerIntervalResponse.class);
        method.setAccessible(true);

        var result = method.invoke(converter, response);

        assertInstanceOf(ProducerIntervalResponseDTO.class, result);
        var dto = (ProducerIntervalResponseDTO) result;
        assertEquals(1, dto.min().size());
        assertTrue(dto.max().isEmpty());
    }

    @Test
    @DisplayName("Should call convertProducerInterval method directly")
    void shouldCallConvertProducerIntervalMethodDirectly() throws Exception {
        var interval = ProducerInterval.of("Producer", 2010, 2015);
        Method method = ConverterDtoAdapter.class.getDeclaredMethod("convertProducerInterval", ProducerInterval.class);
        method.setAccessible(true);

        var result = method.invoke(converter, interval);

        assertInstanceOf(ProducerIntervalDTO.class, result);
        var dto = (ProducerIntervalDTO) result;
        assertEquals("Producer", dto.producer());
        assertEquals(5, dto.interval());
    }

    @Test
    @DisplayName("Should call convertProducerIntervalResponse with null response")
    void shouldCallConvertProducerIntervalResponseWithNullResponse() throws Exception {
        Method method = ConverterDtoAdapter.class.getDeclaredMethod("convertProducerIntervalResponse", ProducerIntervalResponse.class);
        method.setAccessible(true);

        var result = method.invoke(converter, (ProducerIntervalResponse) null);

        assertInstanceOf(ProducerIntervalResponseDTO.class, result);
        var dto = (ProducerIntervalResponseDTO) result;
        assertTrue(dto.min().isEmpty());
        assertTrue(dto.max().isEmpty());
    }

    @Test
    @DisplayName("Should call convertProducerInterval with null interval")
    void shouldCallConvertProducerIntervalWithNullInterval() throws Exception {
        Method method = ConverterDtoAdapter.class.getDeclaredMethod("convertProducerInterval", ProducerInterval.class);
        method.setAccessible(true);

        var result = method.invoke(converter, (ProducerInterval) null);

        assertInstanceOf(ProducerIntervalDTO.class, result);
        var dto = (ProducerIntervalDTO) result;
        assertEquals("Unknown", dto.producer());
        assertEquals(0, dto.interval());
        assertEquals(0, dto.previousWin());
        assertEquals(0, dto.followingWin());
    }

    @Test
    @DisplayName("Should call convertProducerIntervalResponse with response containing null intervals in lists")
    void shouldCallConvertProducerIntervalResponseWithNullIntervalsInLists() throws Exception {
        var interval1 = ProducerInterval.of("Producer 1", 2010, 2015);
        var listWithNull = new java.util.ArrayList<ProducerInterval>();
        listWithNull.add(interval1);
        listWithNull.add(null);
        var response = new ProducerIntervalResponse(listWithNull, List.of());
        Method method = ConverterDtoAdapter.class.getDeclaredMethod("convertProducerIntervalResponse", ProducerIntervalResponse.class);
        method.setAccessible(true);

        var result = method.invoke(converter, response);

        assertInstanceOf(ProducerIntervalResponseDTO.class, result);
        var dto = (ProducerIntervalResponseDTO) result;
        assertEquals(1, dto.min().size());
        assertTrue(dto.max().isEmpty());
    }

}

