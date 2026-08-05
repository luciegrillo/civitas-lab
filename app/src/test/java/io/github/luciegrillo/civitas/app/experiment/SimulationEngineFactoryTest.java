package io.github.luciegrillo.civitas.app.experiment;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.github.luciegrillo.civitas.app.config.InitializationSpec;
import io.github.luciegrillo.civitas.app.config.InitializationType;
import io.github.luciegrillo.civitas.app.config.LatticeSpec;
import io.github.luciegrillo.civitas.app.config.ScenarioSpec;
import io.github.luciegrillo.civitas.app.config.UpdateScheduleSpec;
import io.github.luciegrillo.civitas.app.config.UpdateScheduleType;
import io.github.luciegrillo.civitas.core.BoundaryCondition;
import io.github.luciegrillo.civitas.core.Lattice;
import io.github.luciegrillo.civitas.core.RandomSequentialSpatialGameEngine;
import io.github.luciegrillo.civitas.core.SimulationConfig;
import io.github.luciegrillo.civitas.core.SimulationEngine;
import io.github.luciegrillo.civitas.core.SpatialGameEngine;
import io.github.luciegrillo.civitas.core.Strategy;
import io.github.luciegrillo.civitas.core.WeakPrisonersDilemma;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimulationEngineFactoryTest {

    @Test
    void selectsTheConfiguredEngine() {
        Lattice initial = Lattice.filled(5, 5, Strategy.COOPERATE);
        SimulationConfig config = new SimulationConfig(
                5,
                5,
                BoundaryCondition.TOROIDAL,
                new WeakPrisonersDilemma(1.85),
                true);

        SimulationEngine synchronous = SimulationEngineFactory.create(
                scenario(UpdateScheduleType.SYNCHRONOUS), config, initial, 1L);
        SimulationEngine randomSequential = SimulationEngineFactory.create(
                scenario(UpdateScheduleType.RANDOM_SEQUENTIAL), config, initial, 2L);

        assertInstanceOf(SpatialGameEngine.class, synchronous);
        assertInstanceOf(RandomSequentialSpatialGameEngine.class, randomSequential);
    }

    private static ScenarioSpec scenario(UpdateScheduleType type) {
        return new ScenarioSpec(
                "baseline",
                "paired",
                new LatticeSpec(5, 5, BoundaryCondition.TOROIDAL),
                new InitializationSpec(InitializationType.BERNOULLI, 0.9),
                true,
                List.of(1.85),
                1,
                1,
                0,
                List.of(),
                new UpdateScheduleSpec(type));
    }
}
