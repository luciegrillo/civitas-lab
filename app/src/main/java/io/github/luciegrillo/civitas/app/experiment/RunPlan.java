package io.github.luciegrillo.civitas.app.experiment;

import io.github.luciegrillo.civitas.app.config.ScenarioSpec;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Fully expanded identity and parameters for one independent run.
 */
public record RunPlan(
        ScenarioSpec scenario,
        double temptation,
        int replicate,
        long seed,
        String runId) {

    public RunPlan {
        Objects.requireNonNull(scenario, "scenario");
        Objects.requireNonNull(runId, "runId");
        if (replicate < 0) {
            throw new IllegalArgumentException("replicate must not be negative");
        }
    }

    static String createId(String scenarioId, double temptation, int replicate) {
        String value = BigDecimal.valueOf(temptation)
                .stripTrailingZeros()
                .toPlainString()
                .replace('.', '_');
        return "%s__b-%s__r-%03d".formatted(scenarioId, value, replicate);
    }
}
