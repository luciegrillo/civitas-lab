package io.github.luciegrillo.civitas.app.experiment;

/**
 * Distribution summary across replicates of one scenario and temptation.
 */
public record AggregateSummary(
        String scenarioId,
        double temptation,
        int runs,
        double finalMean,
        double finalQ05,
        double finalQ25,
        double finalMedian,
        double finalQ75,
        double finalQ95,
        double allCooperateRate,
        double allDefectRate,
        double mixedRate,
        double measurementMeanCooperatorFraction,
        double measurementMeanFlipRate) {
}
