package io.github.luciegrillo.civitas.app.experiment;

import io.github.luciegrillo.civitas.app.artifact.ChartRenderer;
import io.github.luciegrillo.civitas.app.artifact.ChecksumManifest;
import io.github.luciegrillo.civitas.app.artifact.CsvArtifacts;
import io.github.luciegrillo.civitas.app.artifact.JsonArtifacts;
import io.github.luciegrillo.civitas.app.artifact.OutputWorkspace;
import io.github.luciegrillo.civitas.app.artifact.Provenance;
import io.github.luciegrillo.civitas.app.config.ExperimentSpec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Coordinates independent runs and deterministic aggregate output.
 */
public final class ExperimentRunner {

    /**
     * Executes a validated experiment.
     */
    public ExecutionReport run(
            ExperimentSpec experiment, Path output, boolean overwrite) throws IOException {
        long started = System.nanoTime();
        try (OutputWorkspace workspace = OutputWorkspace.prepare(output, overwrite)) {
            JsonArtifacts.write(workspace.root().resolve("resolved-experiment.json"), experiment);
            JsonArtifacts.write(workspace.root().resolve("provenance.json"), Provenance.capture());

            List<RunPlan> plans = RunPlanner.expand(experiment);
            ArrayList<Future<RunResult>> futures = new ArrayList<>(plans.size());
            ArrayList<RunResult> results = new ArrayList<>(plans.size());

            try (ExecutorService executor =
                    Executors.newFixedThreadPool(Math.min(experiment.parallelism(), plans.size()))) {
                for (RunPlan plan : plans) {
                    futures.add(executor.submit(new SimulationTask(plan, workspace)));
                }
                for (Future<RunResult> future : futures) {
                    results.add(await(future));
                }
            }

            List<RunResult> immutableResults = List.copyOf(results);
            List<AggregateSummary> aggregates = ResultAggregator.aggregate(immutableResults);
            CsvArtifacts.writeRunSummaries(
                    workspace.root().resolve("summary.csv"), immutableResults);
            CsvArtifacts.writeAggregates(
                    workspace.root().resolve("aggregate.csv"), aggregates);
            ChartRenderer.writeAll(
                    workspace.root().resolve("figures"), immutableResults, aggregates);
            ChecksumManifest.write(workspace.root());

            Path publishedOutput = workspace.publish();
            long elapsedMillis = (System.nanoTime() - started) / 1_000_000;
            return new ExecutionReport(publishedOutput, plans.size(), elapsedMillis);
        }
    }

    private static RunResult await(Future<RunResult> future) throws IOException {
        try {
            return future.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("experiment execution was interrupted", exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("simulation run failed: " + cause.getMessage(), cause);
        }
    }
}
