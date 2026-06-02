package io.github.luciegrillo.civitas.app.artifact;

import io.github.luciegrillo.civitas.app.experiment.AggregateSummary;
import io.github.luciegrillo.civitas.app.experiment.RunResult;
import io.github.luciegrillo.civitas.app.experiment.TimePoint;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

/**
 * Java-only charts for repository and release artifacts.
 */
public final class ChartRenderer {
    private ChartRenderer() {
    }

    public static void writeAll(
            Path figuresDirectory,
            List<RunResult> results,
            List<AggregateSummary> aggregates) throws IOException {
        writeFinalDistributions(figuresDirectory, aggregates);
        writeTimeSeries(figuresDirectory, results);
        if (results.stream().map(result -> result.plan().scenario().id()).distinct().count() > 1) {
            writeScenarioComparison(figuresDirectory, aggregates);
        }
    }

    private static void writeScenarioComparison(
            Path directory, List<AggregateSummary> aggregates) throws IOException {
        LinkedHashMap<String, List<AggregateSummary>> byScenario = new LinkedHashMap<>();
        for (AggregateSummary aggregate : aggregates) {
            byScenario.computeIfAbsent(
                    aggregate.scenarioId(), ignored -> new ArrayList<>()).add(aggregate);
        }
        XYChart chart = chart(
                "Final cooperation across scenarios",
                "Temptation (b)",
                "Median final cooperator fraction");
        for (Map.Entry<String, List<AggregateSummary>> entry : byScenario.entrySet()) {
            chart.addSeries(
                    entry.getKey(),
                    entry.getValue().stream()
                            .map(AggregateSummary::temptation)
                            .toList(),
                    entry.getValue().stream()
                            .map(AggregateSummary::finalMedian)
                            .toList());
        }
        save(chart, directory.resolve("scenario-comparison.png"));
    }

    private static void writeFinalDistributions(
            Path directory, List<AggregateSummary> aggregates) throws IOException {
        LinkedHashMap<String, List<AggregateSummary>> byScenario = new LinkedHashMap<>();
        for (AggregateSummary aggregate : aggregates) {
            byScenario.computeIfAbsent(
                    aggregate.scenarioId(), ignored -> new ArrayList<>()).add(aggregate);
        }
        for (Map.Entry<String, List<AggregateSummary>> entry : byScenario.entrySet()) {
            List<AggregateSummary> values = entry.getValue();
            XYChart chart = chart(
                    "Final cooperation distribution: " + entry.getKey(),
                    "Temptation (b)",
                    "Cooperator fraction");
            chart.addSeries(
                    "median",
                    values.stream().map(AggregateSummary::temptation).toList(),
                    values.stream().map(AggregateSummary::finalMedian).toList());
            chart.addSeries(
                    "q25",
                    values.stream().map(AggregateSummary::temptation).toList(),
                    values.stream().map(AggregateSummary::finalQ25).toList());
            chart.addSeries(
                    "q75",
                    values.stream().map(AggregateSummary::temptation).toList(),
                    values.stream().map(AggregateSummary::finalQ75).toList());
            save(chart, directory.resolve(entry.getKey() + "-final-distribution.png"));
        }
    }

    private static void writeTimeSeries(Path directory, List<RunResult> results)
            throws IOException {
        LinkedHashMap<String, List<RunResult>> byScenario = new LinkedHashMap<>();
        for (RunResult result : results) {
            byScenario.computeIfAbsent(
                    result.plan().scenario().id(), ignored -> new ArrayList<>()).add(result);
        }

        for (Map.Entry<String, List<RunResult>> scenarioEntry : byScenario.entrySet()) {
            LinkedHashMap<Double, List<RunResult>> byTemptation = new LinkedHashMap<>();
            for (RunResult result : scenarioEntry.getValue()) {
                byTemptation.computeIfAbsent(
                        result.plan().temptation(), ignored -> new ArrayList<>()).add(result);
            }

            XYChart chart = chart(
                    "Cooperation over time: " + scenarioEntry.getKey(),
                    "Tick",
                    "Mean cooperator fraction");
            for (Map.Entry<Double, List<RunResult>> temptationEntry : byTemptation.entrySet()) {
                List<RunResult> runs = temptationEntry.getValue();
                int pointCount = runs.getFirst().timeSeries().size();
                ArrayList<Integer> ticks = new ArrayList<>(pointCount);
                ArrayList<Double> means = new ArrayList<>(pointCount);
                for (int index = 0; index < pointCount; index++) {
                    ticks.add(runs.getFirst().timeSeries().get(index).tick());
                    double sum = 0.0;
                    for (RunResult run : runs) {
                        TimePoint point = run.timeSeries().get(index);
                        sum += point.cooperatorFraction();
                    }
                    means.add(sum / runs.size());
                }
                chart.addSeries("b=" + temptationEntry.getKey(), ticks, means);
            }
            save(chart, directory.resolve(scenarioEntry.getKey() + "-timeseries.png"));
        }
    }

    private static XYChart chart(String title, String xAxis, String yAxis) {
        XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(650)
                .title(title)
                .xAxisTitle(xAxis)
                .yAxisTitle(yAxis)
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(1.0);
        return chart;
    }

    private static void save(XYChart chart, Path path) throws IOException {
        BitmapEncoder.saveBitmap(
                chart, path.toString(), BitmapEncoder.BitmapFormat.PNG);
    }
}
