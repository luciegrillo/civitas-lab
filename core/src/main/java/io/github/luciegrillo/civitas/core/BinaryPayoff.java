package io.github.luciegrillo.civitas.core;

/**
 * Payoff rule for a two-strategy spatial game.
 */
public interface BinaryPayoff {

    /**
     * Stable human-readable identifier for provenance and documentation.
     *
     * @return model identifier
     */
    String name();

    /**
     * Returns the focal player's payoff for one pairwise interaction.
     *
     * @param focal focal strategy
     * @param opponent opponent strategy
     * @return interaction payoff
     */
    double payoff(Strategy focal, Strategy opponent);

    /**
     * Returns the accumulated payoff against counted cooperative and defecting
     * opponents.
     *
     * @param focalCode focal strategy code
     * @param cooperativeOpponents number of cooperative opponents
     * @param defectingOpponents number of defecting opponents
     * @return accumulated payoff
     */
    default double accumulatedPayoff(
            byte focalCode, int cooperativeOpponents, int defectingOpponents) {
        if (cooperativeOpponents < 0 || defectingOpponents < 0) {
            throw new IllegalArgumentException("opponent counts must not be negative");
        }
        Strategy focal = Strategy.fromCode(focalCode);
        return cooperativeOpponents * payoff(focal, Strategy.COOPERATE)
                + defectingOpponents * payoff(focal, Strategy.DEFECT);
    }
}
