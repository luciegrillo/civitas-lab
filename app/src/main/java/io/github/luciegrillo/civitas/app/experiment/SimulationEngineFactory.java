package io.github.luciegrillo.civitas.app.experiment;

import io.github.luciegrillo.civitas.app.config.ScenarioSpec;
import io.github.luciegrillo.civitas.core.Lattice;
import io.github.luciegrillo.civitas.core.RandomSequentialSpatialGameEngine;
import io.github.luciegrillo.civitas.core.SimulationConfig;
import io.github.luciegrillo.civitas.core.SimulationEngine;
import io.github.luciegrillo.civitas.core.SpatialGameEngine;
import java.util.Objects;

/**
 * Selects the core engine declared by one validated scenario.
 */
final class SimulationEngineFactory {
    private SimulationEngineFactory() {
    }

    static SimulationEngine create(
            ScenarioSpec scenario,
            SimulationConfig config,
            Lattice initialState,
            long scheduleSeed) {
        Objects.requireNonNull(scenario, "scenario");
        return switch (scenario.effectiveUpdateSchedule().type()) {
            case SYNCHRONOUS -> new SpatialGameEngine(config, initialState);
            case RANDOM_SEQUENTIAL -> new RandomSequentialSpatialGameEngine(
                    config, initialState, scheduleSeed);
        };
    }
}
