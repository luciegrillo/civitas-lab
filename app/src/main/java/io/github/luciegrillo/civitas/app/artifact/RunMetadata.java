package io.github.luciegrillo.civitas.app.artifact;

import io.github.luciegrillo.civitas.app.config.InitializationSpec;
import io.github.luciegrillo.civitas.app.config.LatticeSpec;

/**
 * Self-contained parameters for reproducing one expanded run.
 */
public record RunMetadata(
        String runId,
        String scenarioId,
        String seedGroup,
        double temptation,
        int replicate,
        long seed,
        LatticeSpec lattice,
        InitializationSpec initialization,
        boolean selfInteraction,
        int ticks,
        int measurementStart) {
}
