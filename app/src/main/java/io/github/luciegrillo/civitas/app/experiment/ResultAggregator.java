package io.github.luciegrillo.civitas.app.experiment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Aggregates run-level distributions without assuming unimodality.
 */
public final class ResultAggregator {
    private ResultAggregator() {
    }

    /**
     * Groups results in first-observed scenario and temptation order.
     */
    public static List<AggregateSummary> aggregate(List<RunResult> results) {
        LinkedHashMap<Key, List<RunSummary>> groups = new LinkedHashMap<>();
        for (RunResult result : results) {
            RunSummary summary = result.summary();
            groups.computeIfAbsent(
                            new Key(summary.scenarioId(), summary.temptation()),
                            ignored -> new ArrayList<>())
                    .add(summary);
        }

        ArrayList<AggregateSummary> aggregates = new ArrayList<>(groups.size());
        for (Map.Entry<Key, List<RunSummary>> entry : groups.entrySet()) {
            List<RunSummary> summaries = entry.getValue();
            List<Double> finalFractions = summaries.stream()
                    .map(RunSummary::finalCooperatorFraction)
                    .toList();
            List<Double> measurementCooperation = summaries.stream()
                    .map(RunSummary::measurementMeanCooperatorFraction)
                    .toList();
            List<Double> measurementFlips = summaries.stream()
                    .map(RunSummary::measurementMeanFlipRate)
                    .toList();

            int allCooperate = 0;
            int allDefect = 0;
            for (RunSummary summary : summaries) {
                if (summary.finalPopulationState() == PopulationState.ALL_COOPERATE) {
                    allCooperate++;
                } else if (summary.finalPopulationState() == PopulationState.ALL_DEFECT) {
                    allDefect++;
                }
            }
            int count = summaries.size();
            aggregates.add(new AggregateSummary(
                    entry.getKey().scenarioId,
                    entry.getKey().temptation,
                    count,
                    Statistics.mean(finalFractions),
                    Statistics.quantile(finalFractions, 0.05),
                    Statistics.quantile(finalFractions, 0.25),
                    Statistics.quantile(finalFractions, 0.50),
                    Statistics.quantile(finalFractions, 0.75),
                    Statistics.quantile(finalFractions, 0.95),
                    (double) allCooperate / count,
                    (double) allDefect / count,
                    (double) (count - allCooperate - allDefect) / count,
                    Statistics.mean(measurementCooperation),
                    Statistics.mean(measurementFlips)));
        }
        return List.copyOf(aggregates);
    }

    private record Key(String scenarioId, double temptation) {
    }
}
