package golden.raspberry.awards.integration.adapter.driving.rest.converter;

import golden.raspberry.awards.adapter.driving.rest.converter.ConverterDtoAdapter;
import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerInterval;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerIntervalResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration-test")
class ConverterDtoAdapterIT {

    @Autowired
    private ConverterDtoAdapter converter;

    @Test
    void shouldConvertMovieWithIdToDTO() {
        var movie = new MovieWithId(1L, 2020, "Test Movie", "Studio", "Producer", true);
        var dto = (MovieDTO) converter.toDTO(movie);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals(2020, dto.year());
        assertEquals("Test Movie", dto.title());
        assertEquals("Studio", dto.studios());
        assertEquals("Producer", dto.producers());
        assertTrue(dto.winner());
    }

    @Test
    void shouldConvertProducerIntervalResponseToDTO() {
        var interval1 = new ProducerInterval("Producer 1", 1, 2008, 2009);
        var interval2 = new ProducerInterval("Producer 2", 99, 1900, 1999);
        var response = new ProducerIntervalResponse(
                List.of(interval1),
                List.of(interval2)
        );

        var dto = (ProducerIntervalResponseDTO) converter.toDTO(response);

        assertNotNull(dto);
        assertEquals(1, dto.min().size());
        assertEquals(1, dto.max().size());
        assertEquals("Producer 1", dto.min().get(0).producer());
        assertEquals("Producer 2", dto.max().get(0).producer());
    }

    @Test
    void shouldConvertProducerIntervalToDTO() {
        var interval = new ProducerInterval("Producer", 5, 2010, 2015);
        var dto = (ProducerIntervalDTO) converter.toDTO(interval);

        assertNotNull(dto);
        assertEquals("Producer", dto.producer());
        assertEquals(5, dto.interval());
        assertEquals(2010, dto.previousWin());
        assertEquals(2015, dto.followingWin());
    }
}

