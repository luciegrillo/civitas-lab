package io.github.luciegrillo.civitas.core;

import java.util.Objects;

/**
 * Deterministic synchronous engine for a spatial weak Prisoner's Dilemma.
 *
 * <p>Each step has two complete phases. First, payoffs are computed from the
 * current lattice. Second, strategies are written to a separate buffer. No
 * strategy chosen at generation {@code t+1} can affect another site during the
 * same generation.</p>
 */
public final class SpatialGameEngine {
    private final SimulationConfig config;
    private final MooreNeighborhood neighborhoods;
    private final double[] payoffs;

    private byte[] current;
    private byte[] next;
    private long generation;
    private StepMetrics metrics;

    /**
     * Creates an engine at generation zero.
     *
     * @param config model parameters
     * @param initialState initial lattice
     */
    public SpatialGameEngine(SimulationConfig config, Lattice initialState) {
        this.config = Objects.requireNonNull(config, "config");
        Objects.requireNonNull(initialState, "initialState");
        if (initialState.width() != config.width()
                || initialState.height() != config.height()) {
            throw new IllegalArgumentException(
                    "initial lattice dimensions do not match the simulation config");
        }

        neighborhoods = MooreNeighborhood.create(
                config.width(), config.height(), config.boundaryCondition());
        current = initialState.copyCodes();
        next = new byte[current.length];
        payoffs = new double[current.length];
        int cooperators = initialState.count(Strategy.COOPERATE);
        metrics = new StepMetrics(0, cooperators, current.length - cooperators, 0);
    }

    /**
     * Advances all sites by one synchronous generation.
     *
     * @return metrics for the resulting state
     */
    public StepMetrics step() {
        computePayoffs();

        int cooperators = 0;
        int changes = 0;
        for (int site = 0; site < current.length; site++) {
            byte focal = current[site];
            double bestCooperatorPayoff =
                    focal == Strategy.COOPERATE.code()
                            ? payoffs[site]
                            : Double.NEGATIVE_INFINITY;
            double bestDefectorPayoff =
                    focal == Strategy.DEFECT.code()
                            ? payoffs[site]
                            : Double.NEGATIVE_INFINITY;

            int neighborCount = neighborhoods.count(site);
            for (int offset = 0; offset < neighborCount; offset++) {
                int candidate = neighborhoods.neighbor(site, offset);
                if (current[candidate] == Strategy.COOPERATE.code()) {
                    bestCooperatorPayoff = Math.max(
                            bestCooperatorPayoff, payoffs[candidate]);
                } else {
                    bestDefectorPayoff = Math.max(
                            bestDefectorPayoff, payoffs[candidate]);
                }
            }

            byte selected = selectStrategy(
                    focal, bestCooperatorPayoff, bestDefectorPayoff);
            next[site] = selected;
            if (selected == Strategy.COOPERATE.code()) {
                cooperators++;
            }
            if (selected != focal) {
                changes++;
            }
        }

        byte[] previous = current;
        current = next;
        next = previous;
        generation++;
        metrics = new StepMetrics(
                generation, cooperators, current.length - cooperators, changes);
        return metrics;
    }

    /**
     * Returns metrics for the current state.
     *
     * @return immutable metrics
     */
    public StepMetrics metrics() {
        return metrics;
    }

    /**
     * Copies the current lattice into an immutable snapshot.
     *
     * @return current generation and lattice
     */
    public SimulationSnapshot snapshot() {
        return new SimulationSnapshot(
                generation,
                Lattice.copyOfInternal(config.width(), config.height(), current));
    }

    private void computePayoffs() {
        for (int site = 0; site < current.length; site++) {
            int cooperativeOpponents = 0;
            int neighborCount = neighborhoods.count(site);
            for (int offset = 0; offset < neighborCount; offset++) {
                int opponent = neighborhoods.neighbor(site, offset);
                if (current[opponent] == Strategy.COOPERATE.code()) {
                    cooperativeOpponents++;
                }
            }
            if (config.selfInteraction()
                    && current[site] == Strategy.COOPERATE.code()) {
                cooperativeOpponents++;
            }
            payoffs[site] = config.game().accumulatedPayoff(
                    current[site], cooperativeOpponents);
        }
    }

    static byte selectStrategy(
            byte focal, double bestCooperatorPayoff, double bestDefectorPayoff) {
        int comparison = Double.compare(bestCooperatorPayoff, bestDefectorPayoff);
        if (comparison > 0) {
            return Strategy.COOPERATE.code();
        }
        if (comparison < 0) {
            return Strategy.DEFECT.code();
        }
        return focal;
    }
}
