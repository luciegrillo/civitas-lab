package io.github.luciegrillo.civitas.core;

/**
 * Deterministic imitation of the locally best represented strategy.
 */
public enum UnconditionalImitation implements StrategyUpdateRule {
    /** Shared stateless instance. */
    INSTANCE;

    @Override
    public byte select(byte focal, double bestCooperatorPayoff, double bestDefectorPayoff) {
        Strategy.fromCode(focal);
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
