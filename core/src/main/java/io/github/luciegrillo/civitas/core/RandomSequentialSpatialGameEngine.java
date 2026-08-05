package io.github.luciegrillo.civitas.core;

import java.util.Objects;

/**
 * Deterministic random-sequential engine for a binary spatial game.
 *
 * <p>Each step shuffles all sites, visits each site exactly once, recomputes
 * local candidate payoffs from the current lattice, and writes the selected
 * strategy immediately. Sites visited later in the sweep therefore observe
 * changes made earlier in the same step.</p>
 */
public final class RandomSequentialSpatialGameEngine implements SimulationEngine {
    private final SimulationConfig config;
    private final MooreNeighborhood neighborhoods;
    private final ShuffledSiteScheduler scheduler;
    private final int[] visitOrder;
    private final byte[] current;

    private long generation;
    private int cooperators;
    private StepMetrics metrics;

    /**
     * Creates an engine at generation zero.
     *
     * @param config model parameters
     * @param initialState initial lattice
     * @param scheduleSeed seed used only to order site updates
     */
    public RandomSequentialSpatialGameEngine(
            SimulationConfig config, Lattice initialState, long scheduleSeed) {
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
        visitOrder = new int[current.length];
        scheduler = new ShuffledSiteScheduler(current.length, scheduleSeed);
        cooperators = initialState.count(Strategy.COOPERATE);
        metrics = new StepMetrics(0, cooperators, current.length - cooperators, 0);
    }

    /**
     * Advances all sites by one shuffled in-place sweep.
     *
     * @return metrics for the resulting state
     */
    @Override
    public StepMetrics step() {
        scheduler.fillNextSweep(visitOrder);

        int changes = 0;
        for (int site : visitOrder) {
            byte focal = current[site];
            double focalPayoff = payoffAt(site);
            double bestCooperatorPayoff =
                    focal == Strategy.COOPERATE.code()
                            ? focalPayoff
                            : Double.NEGATIVE_INFINITY;
            double bestDefectorPayoff =
                    focal == Strategy.DEFECT.code()
                            ? focalPayoff
                            : Double.NEGATIVE_INFINITY;

            int neighborCount = neighborhoods.count(site);
            for (int offset = 0; offset < neighborCount; offset++) {
                int candidate = neighborhoods.neighbor(site, offset);
                double candidatePayoff = payoffAt(candidate);
                if (current[candidate] == Strategy.COOPERATE.code()) {
                    bestCooperatorPayoff = Math.max(
                            bestCooperatorPayoff, candidatePayoff);
                } else {
                    bestDefectorPayoff = Math.max(
                            bestDefectorPayoff, candidatePayoff);
                }
            }

            byte selected = config.updateRule().select(
                    focal, bestCooperatorPayoff, bestDefectorPayoff);
            if (selected != focal) {
                current[site] = selected;
                changes++;
                if (selected == Strategy.COOPERATE.code()) {
                    cooperators++;
                } else {
                    cooperators--;
                }
            }
        }

        generation++;
        metrics = new StepMetrics(
                generation, cooperators, current.length - cooperators, changes);
        return metrics;
    }

    @Override
    public StepMetrics metrics() {
        return metrics;
    }

    @Override
    public SimulationSnapshot snapshot() {
        return new SimulationSnapshot(
                generation,
                Lattice.copyOfInternal(config.width(), config.height(), current));
    }

    private double payoffAt(int site) {
        int cooperativeOpponents = 0;
        int defectingOpponents = 0;
        int neighborCount = neighborhoods.count(site);
        for (int offset = 0; offset < neighborCount; offset++) {
            int opponent = neighborhoods.neighbor(site, offset);
            if (current[opponent] == Strategy.COOPERATE.code()) {
                cooperativeOpponents++;
            } else {
                defectingOpponents++;
            }
        }
        if (config.selfInteraction()) {
            if (current[site] == Strategy.COOPERATE.code()) {
                cooperativeOpponents++;
            } else {
                defectingOpponents++;
            }
        }
        return config.game().accumulatedPayoff(
                current[site], cooperativeOpponents, defectingOpponents);
    }
}
