package io.github.luciegrillo.civitas.app.experiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.github.luciegrillo.civitas.app.config.ExperimentSpec;
import io.github.luciegrillo.civitas.app.config.InitializationSpec;
import io.github.luciegrillo.civitas.app.config.InitializationType;
import io.github.luciegrillo.civitas.app.config.LatticeSpec;
import io.github.luciegrillo.civitas.app.config.ScenarioSpec;
import io.github.luciegrillo.civitas.app.config.UpdateScheduleSpec;
import io.github.luciegrillo.civitas.app.config.UpdateScheduleType;
import io.github.luciegrillo.civitas.core.BoundaryCondition;
import io.github.luciegrillo.civitas.core.SeedDerivation;
import java.util.List;
import org.junit.jupiter.api.Test;

class RunPlannerTest {

    @Test
    void separatesInitializationAndScheduleSeedsAcrossPairedScenarios() {
        ExperimentSpec experiment = new ExperimentSpec(
                ExperimentSpec.CURRENT_SCHEMA_VERSION,
                "schedule-pair",
                42L,
                1,
                List.of(
                        scenario("synchronous", UpdateScheduleType.SYNCHRONOUS),
                        scenario("random-sequential", UpdateScheduleType.RANDOM_SEQUENTIAL)));

        List<RunPlan> plans = RunPlanner.expand(experiment);
        RunPlan synchronous = plans.get(0);
        RunPlan asynchronous = plans.get(1);

        assertEquals(synchronous.initializationSeed(), asynchronous.initializationSeed());
        assertEquals(synchronous.scheduleSeed(), asynchronous.scheduleSeed());
        assertNotEquals(synchronous.initializationSeed(), synchronous.scheduleSeed());
        assertEquals(
                SeedDerivation.derive(42L, "paired", 0),
                synchronous.initializationSeed());
        assertEquals(
                SeedDerivation.derive(42L, "schedule", "paired", 0),
                synchronous.scheduleSeed());
        assertEquals(synchronous.initializationSeed(), synchronous.seed());
    }

    private static ScenarioSpec scenario(String id, UpdateScheduleType scheduleType) {
        return new ScenarioSpec(
                id,
                "paired",
                new LatticeSpec(9, 9, BoundaryCondition.TOROIDAL),
                new InitializationSpec(InitializationType.BERNOULLI, 0.9),
                true,
                List.of(1.85),
                1,
                5,
                2,
                List.of(0, 5),
                new UpdateScheduleSpec(scheduleType));
    }
}
