package io.github.luciegrillo.civitas.app.artifact;

import io.github.luciegrillo.civitas.app.config.ExperimentSpec;
import io.github.luciegrillo.civitas.app.experiment.AggregateSummary;
import io.github.luciegrillo.civitas.app.experiment.RunResult;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Coordinates experiment-level scientific artifact rendering.
 */
public final class ExperimentArtifactWriter {
    private final OutputWorkspace workspace;

    public ExperimentArtifactWriter(OutputWorkspace workspace) {
        this.workspace = Objects.requireNonNull(workspace, "workspace");
    }

    /**
     * Writes deterministic aggregate tables and figures.
     */
    public void write(
            ExperimentSpec experiment,
            List<RunResult> results,
            List<AggregateSummary> aggregates) throws IOException {
        Objects.requireNonNull(experiment, "experiment");
        Objects.requireNonNull(results, "results");
        Objects.requireNonNull(aggregates, "aggregates");

        CsvArtifacts.writeRunSummaries(
                workspace.root().resolve("summary.csv"), results);
        CsvArtifacts.writeAggregates(
                workspace.root().resolve("aggregate.csv"), aggregates);
        ChartRenderer.writeAll(
                workspace.root().resolve("figures"), results, aggregates);
    }
}
