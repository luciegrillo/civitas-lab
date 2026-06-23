package io.github.luciegrillo.civitas.core;

import java.util.Objects;

/**
 * Immutable parameters for one spatial simulation.
 *
 * @param width lattice width
 * @param height lattice height
 * @param boundaryCondition edge behavior
 * @param game pairwise payoff model
 * @param updateRule local strategy selection rule
 * @param selfInteraction whether a player also plays against itself
 */
public record SimulationConfig(
        int width,
        int height,
        BoundaryCondition boundaryCondition,
        BinaryPayoff game,
        StrategyUpdateRule updateRule,
        boolean selfInteraction) {

    /**
     * Creates a simulation that uses deterministic unconditional imitation.
     *
     * @param width lattice width
     * @param height lattice height
     * @param boundaryCondition edge behavior
     * @param game pairwise payoff model
     * @param selfInteraction whether a player also plays against itself
     */
    public SimulationConfig(
            int width,
            int height,
            BoundaryCondition boundaryCondition,
            BinaryPayoff game,
            boolean selfInteraction) {
        this(
                width,
                height,
                boundaryCondition,
                game,
                UnconditionalImitation.INSTANCE,
                selfInteraction);
    }

    /**
     * Creates validated simulation parameters.
     */
    public SimulationConfig {
        if (width < 3 || height < 3) {
            throw new IllegalArgumentException(
                    "width and height must be at least 3 to define a unique Moore neighborhood");
        }
        Objects.requireNonNull(boundaryCondition, "boundaryCondition");
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(updateRule, "updateRule");
        Math.multiplyExact(width, height);
    }

    /**
     * Returns the number of sites in the lattice.
     *
     * @return width multiplied by height
     */
    public int siteCount() {
        return Math.multiplyExact(width, height);
    }
}
