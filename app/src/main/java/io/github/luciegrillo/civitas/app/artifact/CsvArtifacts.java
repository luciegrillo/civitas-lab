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
        boolean scheduleAware = results.stream()
                .anyMatch(result -> result.plan().scenario().updateSchedule() != null);
        StringBuilder csv = new StringBuilder();
        if (scheduleAware) {
            csv.append("scenario_id,update_schedule,temptation,replicate,"
                    + "initialization_seed,schedule_seed,final_cooperator_fraction,"
                    + "measurement_mean_cooperator_fraction,measurement_mean_flip_rate,"
                    + "final_population_state\n");
        } else {
            csv.append("scenario_id,temptation,replicate,seed,final_cooperator_fraction,"
                    + "measurement_mean_cooperator_fraction,measurement_mean_flip_rate,"
                    + "final_population_state\n");
        }

        for (RunResult result : results) {
            RunSummary summary = result.summary();
            csv.append(summary.scenarioId()).append(',');
            if (scheduleAware) {
                csv.append(result.plan().scenario().effectiveUpdateSchedule().type()).append(',')
                        .append(format(summary.temptation())).append(',')
                        .append(summary.replicate()).append(',')
                        .append(result.plan().initializationSeed()).append(',')
                        .append(result.plan().scheduleSeed()).append(',');
            } else {
                csv.append(format(summary.temptation())).append(',')
                        .append(summary.replicate()).append(',')
                        .append(summary.seed()).append(',');
            }
            csv.append(format(summary.finalCooperatorFraction())).append(',')
                    .append(format(summary.measurementMeanCooperatorFraction())).append(',')
                    .append(format(summary.measurementMeanFlipRate())).append(',')
                    .append(summary.finalPopulationState()).append('\n');
        }
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    public static void writeAggregates(
            Path path, List<AggregateSummary> aggregates) throws IOException {
        boolean scheduleAware = aggregates.stream()
                .anyMatch(aggregate -> aggregate.updateSchedule() != null);
        StringBuilder csv = new StringBuilder();
        if (scheduleAware) {
            csv.append("scenario_id,update_schedule,temptation,runs,final_mean,final_sd,"
                    + "final_q05,final_q25,final_median,final_q75,final_q95,"
                    + "all_cooperate_rate,all_defect_rate,mixed_rate,"
                    + "measurement_mean_cooperator_fraction,"
                    + "measurement_cooperator_fraction_sd,measurement_mean_flip_rate,"
                    + "measurement_flip_rate_sd\n");
        } else {
            csv.append("scenario_id,temptation,runs,final_mean,final_sd,final_q05,final_q25,"
                    + "final_median,final_q75,final_q95,all_cooperate_rate,"
                    + "all_defect_rate,mixed_rate,measurement_mean_cooperator_fraction,"
                    + "measurement_cooperator_fraction_sd,measurement_mean_flip_rate,"
                    + "measurement_flip_rate_sd\n");
        }
        for (AggregateSummary aggregate : aggregates) {
            csv.append(aggregate.scenarioId()).append(',');
            if (scheduleAware) {
                csv.append(aggregate.updateSchedule()).append(',');
            }
            csv.append(format(aggregate.temptation())).append(',')
                    .append(aggregate.runs()).append(',')
                    .append(format(aggregate.finalMean())).append(',')
                    .append(format(aggregate.finalStandardDeviation())).append(',')
                    .append(format(aggregate.finalQ05())).append(',')
                    .append(format(aggregate.finalQ25())).append(',')
                    .append(format(aggregate.finalMedian())).append(',')
                    .append(format(aggregate.finalQ75())).append(',')
                    .append(format(aggregate.finalQ95())).append(',')
                    .append(format(aggregate.allCooperateRate())).append(',')
                    .append(format(aggregate.allDefectRate())).append(',')
                    .append(format(aggregate.mixedRate())).append(',')
                    .append(format(aggregate.measurementMeanCooperatorFraction())).append(',')
                    .append(format(aggregate.measurementCooperatorFractionStandardDeviation()))
                    .append(',')
                    .append(format(aggregate.measurementMeanFlipRate())).append(',')
                    .append(format(aggregate.measurementFlipRateStandardDeviation()))
                    .append('\n');
        }
        Files.writeString(path, csv, StandardCharsets.UTF_8);
    }

    private static String format(double value) {
        return String.format(Locale.ROOT, "%.10f", value);
    }
}
