package io.github.luciegrillo.civitas.app.artifact;

import io.github.luciegrillo.civitas.app.experiment.AggregateSummary;
import io.github.luciegrillo.civitas.app.experiment.RunResult;
import io.github.luciegrillo.civitas.app.experiment.RunSummary;
import io.github.luciegrillo.civitas.app.experiment.TimePoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Locale-independent CSV output.
 */
public final class CsvArtifacts {
    private CsvArtifacts() {
    }

    public static void writeTimeSeries(Path path, List<TimePoint> points) throws IOException {
        StringBuilder csv = new StringBuilder(
                "tick,cooperators,defectors,cooperator_fraction,strategy_changes,flip_rate\n");
        for (TimePoint point : points) {
            csv.append(point.tick()).append(',')
                    .append(point.cooperators()).append(',')
                    .append(point.defectors()).append(',')
                    .append(format(point.cooperatorFraction())).append(',')
                    .append(point.strategyChanges()).append(',')
                    .append(format(point.flipRate())).append('\n');
        }
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    public static void writeRunSummaries(Path path, List<RunResult> results) throws IOException {
        StringBuilder csv = new StringBuilder(
                "scenario_id,temptation,replicate,seed,final_cooperator_fraction,"
                        + "measurement_mean_cooperator_fraction,measurement_mean_flip_rate,"
                        + "final_population_state\n");
        for (RunResult result : results) {
            RunSummary summary = result.summary();
            csv.append(summary.scenarioId()).append(',')
                    .append(format(summary.temptation())).append(',')
                    .append(summary.replicate()).append(',')
                    .append(summary.seed()).append(',')
                    .append(format(summary.finalCooperatorFraction())).append(',')
                    .append(format(summary.measurementMeanCooperatorFraction())).append(',')
                    .append(format(summary.measurementMeanFlipRate())).append(',')
                    .append(summary.finalPopulationState()).append('\n');
        }
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    public static void writeAggregates(
            Path path, List<AggregateSummary> aggregates) throws IOException {
        StringBuilder csv = new StringBuilder(
                "scenario_id,temptation,runs,final_mean,final_q05,final_q25,"
                        + "final_median,final_q75,final_q95,all_cooperate_rate,"
                        + "all_defect_rate,mixed_rate,measurement_mean_cooperator_fraction,"
                        + "measurement_mean_flip_rate\n");
        for (AggregateSummary aggregate : aggregates) {
            csv.append(aggregate.scenarioId()).append(',')
                    .append(format(aggregate.temptation())).append(',')
                    .append(aggregate.runs()).append(',')
                    .append(format(aggregate.finalMean())).append(',')
                    .append(format(aggregate.finalQ05())).append(',')
                    .append(format(aggregate.finalQ25())).append(',')
                    .append(format(aggregate.finalMedian())).append(',')
                    .append(format(aggregate.finalQ75())).append(',')
                    .append(format(aggregate.finalQ95())).append(',')
                    .append(format(aggregate.allCooperateRate())).append(',')
                    .append(format(aggregate.allDefectRate())).append(',')
                    .append(format(aggregate.mixedRate())).append(',')
                    .append(format(aggregate.measurementMeanCooperatorFraction())).append(',')
                    .append(format(aggregate.measurementMeanFlipRate())).append('\n');
        }
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.10f", value);
    }
}
