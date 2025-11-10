package golden.raspberry.awards.core.domain.service;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerInterval;
import golden.raspberry.awards.core.domain.model.valueobject.Producer;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Domain service for calculating producer intervals.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class IntervalProcessorService {

    /**
     * Groups winning movies by producer.
     *
     * @param winningMovies List of winning movies
     * @return Map of producer names to their unique sorted winning years
     */
    public Map<String, List<Integer>> groupWinsByProducer(List<Movie> winningMovies) {
        Objects.requireNonNull(winningMovies, "Winning movies cannot be null");

        return winningMovies.stream()
                .flatMap(movie -> Producer.parseMultiple(movie.producers()).stream()
                        .map(producer -> Map.entry(producer.name(), movie.year())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.collectingAndThen(
                                        Collectors.toSet(),
                                        years -> years.stream()
                                                .sorted()
                                                .toList()
                                )
                        )
                ));
    }

    /**
     * Calculates intervals between consecutive wins for each producer.
     *
     * @param producerWins Map of producer names to their winning years
     * @return List of intervals for all producers
     */
    public List<ProducerInterval> calculateIntervals(Map<String, List<Integer>> producerWins) {
        Objects.requireNonNull(producerWins, "Producer wins cannot be null");

        return producerWins.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= 2)
                .flatMap(entry -> {
                    var producer = entry.getKey();
                    var years = entry.getValue();
                    return IntStream.range(0, years.size() - 1)
                            .mapToObj(i -> ProducerInterval.of(
                                    producer,
                                    years.get(i),
                                    years.get(i + 1)
                            ));
                })
                .toList();
    }
}

