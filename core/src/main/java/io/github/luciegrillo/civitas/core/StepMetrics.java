package io.github.luciegrillo.civitas.core;

/**
 * Aggregate state after a complete simulation step.
 *
 * @param generation current generation, where zero is the initial state
 * @param cooperators number of cooperative sites
 * @param defectors number of defecting sites
 * @param strategyChanges sites whose strategy changed during the last step
 */
public record StepMetrics(
        long generation,
        int cooperators,
        int defectors,
        int strategyChanges) {

    /**
     * Creates validated metrics.
     */
    public StepMetrics {
        if (generation < 0 || cooperators < 0 || defectors < 0 || strategyChanges < 0) {
            throw new IllegalArgumentException("metric values must not be negative");
        }
        if (strategyChanges > cooperators + defectors) {
            throw new IllegalArgumentException("strategyChanges exceeds population");
        }
        if (cooperators + defectors == 0) {
            throw new IllegalArgumentException("population must be positive");
        }
    }

    /**
     * Returns the population size.
     *
     * @return number of sites
     */
    public int population() {
        return Math.addExact(cooperators, defectors);
    }

    /**
     * Returns the fraction of cooperative sites.
     *
     * @return value in {@code [0, 1]}
     */
    public double cooperatorFraction() {
        return (double) cooperators / population();
    }

    /**
     * Returns the fraction of sites changed during the preceding step.
     *
     * @return value in {@code [0, 1]}
     */
    public double flipRate() {
        return (double) strategyChanges / population();
    }
}
