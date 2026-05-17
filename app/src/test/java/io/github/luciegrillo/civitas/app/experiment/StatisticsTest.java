package io.github.luciegrillo.civitas.app.experiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class StatisticsTest {

    @Test
    void computesInterpolatedQuantilesWithoutMutatingInput() {
        List<Double> values = List.of(4.0, 1.0, 3.0, 2.0);

        assertEquals(1.0, Statistics.quantile(values, 0.0));
        assertEquals(2.5, Statistics.quantile(values, 0.5));
        assertEquals(4.0, Statistics.quantile(values, 1.0));
        assertEquals(List.of(4.0, 1.0, 3.0, 2.0), values);
    }

    @Test
    void rejectsEmptySamples() {
        assertThrows(IllegalArgumentException.class, () -> Statistics.mean(List.of()));
        assertThrows(IllegalArgumentException.class, () -> Statistics.quantile(List.of(), 0.5));
    }
}
