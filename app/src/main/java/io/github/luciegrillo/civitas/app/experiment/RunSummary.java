package io.github.luciegrillo.civitas.app.experiment;

/**
 * Compact statistics retained after an independent run.
 */
public record RunSummary(
        String scenarioId,
        double temptation,
        int replicate,
        long seed,
        double finalCooperatorFraction,
        double measurementMeanCooperatorFraction,
        double measurementMeanFlipRate,
        PopulationState finalPopulationState) {
}
