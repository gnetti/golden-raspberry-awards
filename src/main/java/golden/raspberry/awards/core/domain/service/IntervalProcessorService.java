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
        if (winningMovies == null || winningMovies.isEmpty()) {
            return Map.of();
        }

        return winningMovies.stream()
                .filter(Objects::nonNull)
                .filter(movie -> movie.producers() != null && movie.year() != null)
                .flatMap(movie -> {
                    try {
                        return Producer.parseMultiple(movie.producers()).stream()
                                .filter(Objects::nonNull)
                                .map(producer -> Map.entry(producer.name(), movie.year()));
                    } catch (Exception e) {
                        return java.util.stream.Stream.<Map.Entry<String, Integer>>empty();
                    }
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.collectingAndThen(
                                        Collectors.toSet(),
                                        years -> years.stream()
                                                .filter(Objects::nonNull)
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
        if (producerWins == null || producerWins.isEmpty()) {
            return List.of();
        }

        return producerWins.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().size() >= 2)
                .flatMap(entry -> {
                    try {
                        var producer = entry.getKey();
                        var years = entry.getValue();
                        return IntStream.range(0, years.size() - 1)
                                .mapToObj(i -> {
                                    var previousYear = years.get(i);
                                    var followingYear = years.get(i + 1);
                                    if (previousYear == null || followingYear == null) {
                                        return null;
                                    }
                                    try {
                                        return ProducerInterval.of(producer, previousYear, followingYear);
                                    } catch (Exception e) {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull);
                    } catch (Exception e) {
                        return java.util.stream.Stream.<ProducerInterval>empty();
                    }
                })
                .toList();
    }
}

