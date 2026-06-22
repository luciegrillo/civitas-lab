package io.github.luciegrillo.civitas.core;

/**
 * Selects the next strategy from local candidate payoffs.
 */
@FunctionalInterface
public interface StrategyUpdateRule {

    /**
     * Returns the next strategy code for one focal site.
     *
     * @param focal current focal strategy code
     * @param bestCooperatorPayoff best local payoff held by a cooperator
     * @param bestDefectorPayoff best local payoff held by a defector
     * @return next strategy code
     */
    byte select(byte focal, double bestCooperatorPayoff, double bestDefectorPayoff);
}
