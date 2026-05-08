package io.github.luciegrillo.civitas.core;

/**
 * Defines how the square lattice behaves at its edges.
 */
public enum BoundaryCondition {
    /**
     * Opposite edges are connected, forming a torus.
     */
    TOROIDAL,

    /**
     * Out-of-bounds neighbors are absent from both play and imitation.
     */
    BOUNDED
}
