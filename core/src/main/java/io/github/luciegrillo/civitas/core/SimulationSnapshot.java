package io.github.luciegrillo.civitas.core;

import java.util.Objects;

/**
 * Immutable spatial state at one generation.
 *
 * @param generation generation number
 * @param lattice copied lattice state
 */
public record SimulationSnapshot(long generation, Lattice lattice) {

    /**
     * Creates a validated snapshot.
     */
    public SimulationSnapshot {
        if (generation < 0) {
            throw new IllegalArgumentException("generation must not be negative");
        }
        Objects.requireNonNull(lattice, "lattice");
    }
}
