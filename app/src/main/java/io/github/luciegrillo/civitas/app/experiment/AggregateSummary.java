package io.github.luciegrillo.civitas.app.experiment;

import io.github.luciegrillo.civitas.app.config.UpdateScheduleType;

/**
 * Distribution summary across replicates of one scenario, schedule, and
 * temptation value.
 */
public record AggregateSummary(
        String scenarioId,
        UpdateScheduleType updateSchedule,
        double temptation,
        int runs,
        double finalMean,
        double finalStandardDeviation,
        double finalQ05,
        double finalQ25,
        double finalMedian,
        double finalQ75,
        double finalQ95,
        double allCooperateRate,
        double allDefectRate,
        double mixedRate,
        double measurementMeanCooperatorFraction,
        double measurementCooperatorFractionStandardDeviation,
        double measurementMeanFlipRate,
        double measurementFlipRateStandardDeviation) {

    /**
     * Creates a legacy schedule-free summary.
     */
    public AggregateSummary(
            String scenarioId,
            double temptation,
            int runs,
            double finalMean,
            double finalStandardDeviation,
            double finalQ05,
            double finalQ25,
            double finalMedian,
            double finalQ75,
            double finalQ95,
            double allCooperateRate,
            double allDefectRate,
            double mixedRate,
            double measurementMeanCooperatorFraction,
            double measurementCooperatorFractionStandardDeviation,
            double measurementMeanFlipRate,
            double measurementFlipRateStandardDeviation) {
        this(
                scenarioId,
                null,
                temptation,
                runs,
                finalMean,
                finalStandardDeviation,
                finalQ05,
                finalQ25,
                finalMedian,
                finalQ75,
                finalQ95,
                allCooperateRate,
                allDefectRate,
                mixedRate,
                measurementMeanCooperatorFraction,
                measurementCooperatorFractionStandardDeviation,
                measurementMeanFlipRate,
                measurementFlipRateStandardDeviation);
    }
}
