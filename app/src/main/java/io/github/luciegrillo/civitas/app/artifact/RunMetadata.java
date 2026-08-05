package io.github.luciegrillo.civitas.app.artifact;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.luciegrillo.civitas.app.config.InitializationSpec;
import io.github.luciegrillo.civitas.app.config.LatticeSpec;
import io.github.luciegrillo.civitas.app.config.UpdateScheduleSpec;

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
        @JsonInclude(JsonInclude.Include.NON_NULL) UpdateScheduleSpec updateSchedule,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long scheduleSeed,
        LatticeSpec lattice,
        InitializationSpec initialization,
        boolean selfInteraction,
        int ticks,
        int measurementStart) {
}
