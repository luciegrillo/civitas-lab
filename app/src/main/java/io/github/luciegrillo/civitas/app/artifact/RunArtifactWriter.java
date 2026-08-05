package io.github.luciegrillo.civitas.app.artifact;

import io.github.luciegrillo.civitas.app.config.UpdateScheduleSpec;
import io.github.luciegrillo.civitas.app.experiment.RunPlan;
import io.github.luciegrillo.civitas.app.experiment.TimePoint;
import io.github.luciegrillo.civitas.core.Lattice;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes artifacts owned by one independent run.
 */
public final class RunArtifactWriter {
    private final Path runDirectory;

    public RunArtifactWriter(OutputWorkspace workspace, RunPlan plan) throws IOException {
        runDirectory = workspace.runDirectory(plan.runId());
        UpdateScheduleSpec schedule = plan.scenario().updateSchedule();
        RunMetadata metadata = new RunMetadata(
                plan.runId(),
                plan.scenario().id(),
                plan.scenario().seedGroup(),
                plan.temptation(),
                plan.replicate(),
                plan.initializationSeed(),
                schedule,
                schedule == null ? null : plan.scheduleSeed(),
                plan.scenario().lattice(),
                plan.scenario().initialization(),
                plan.scenario().selfInteraction(),
                plan.scenario().ticks(),
                plan.scenario().measurementStart());
        JsonArtifacts.write(runDirectory.resolve("metadata.json"), metadata);
    }

    public void writeTimeSeries(List<TimePoint> points) throws IOException {
        CsvArtifacts.writeTimeSeries(runDirectory.resolve("timeseries.csv"), points);
    }

    public void writeSnapshot(int tick, Lattice previous, Lattice current) throws IOException {
        Path path = runDirectory.resolve("snapshots")
                .resolve("tick-%04d.png".formatted(tick));
        SnapshotRenderer.write(path, previous, current);
    }
}
