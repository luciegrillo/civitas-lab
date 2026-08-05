package io.github.luciegrillo.civitas.core;

/**
 * Advances and observes one spatial simulation.
 */
public interface SimulationEngine {

    /**
     * Advances the model by one complete public time step.
     *
     * @return metrics for the resulting state
     */
    StepMetrics step();

    /**
     * Returns metrics for the current state.
     *
     * @return immutable metrics
     */
    StepMetrics metrics();

    /**
     * Copies the current lattice into an immutable snapshot.
     *
     * @return current generation and lattice
     */
    SimulationSnapshot snapshot();
}
