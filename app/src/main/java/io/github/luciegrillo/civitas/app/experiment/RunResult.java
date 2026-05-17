package io.github.luciegrillo.civitas.app.experiment;

import java.util.List;
import java.util.Objects;

/**
 * Deterministic in-memory result used for aggregate output.
 */
public record RunResult(
        RunPlan plan,
        List<TimePoint> timeSeries,
        RunSummary summary) {

    public RunResult {
        Objects.requireNonNull(plan, "plan");
        timeSeries = List.copyOf(Objects.requireNonNull(timeSeries, "timeSeries"));
        Objects.requireNonNull(summary, "summary");
    }
}
