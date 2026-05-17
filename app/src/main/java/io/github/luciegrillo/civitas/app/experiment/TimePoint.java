package io.github.luciegrillo.civitas.app.experiment;

import io.github.luciegrillo.civitas.core.StepMetrics;

/**
 * Metrics observed at one simulation tick.
 */
public record TimePoint(
        int tick,
        int cooperators,
        int defectors,
        double cooperatorFraction,
        int strategyChanges,
        double flipRate) {

    static TimePoint from(StepMetrics metrics) {
        return new TimePoint(
                Math.toIntExact(metrics.generation()),
                metrics.cooperators(),
                metrics.defectors(),
                metrics.cooperatorFraction(),
                metrics.strategyChanges(),
                metrics.flipRate());
    }
}
