package io.github.luciegrillo.civitas.core;

import java.util.Objects;

/**
 * Immutable parameters for one spatial simulation.
 *
 * @param width lattice width
 * @param height lattice height
 * @param boundaryCondition edge behavior
 * @param game payoff model
 * @param selfInteraction whether a player also plays against itself
 */
public record SimulationConfig(
        int width,
        int height,
        BoundaryCondition boundaryCondition,
        WeakPrisonersDilemma game,
        boolean selfInteraction) {

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
