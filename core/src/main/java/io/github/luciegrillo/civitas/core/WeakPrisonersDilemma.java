package io.github.luciegrillo.civitas.core;

/**
 * The rescaled weak Prisoner's Dilemma used by the spatial Nowak-May model.
 *
 * <p>The payoff matrix is {@code R=1, S=0, T=b, P=0}. The temptation
 * parameter is restricted to the canonical interval {@code 1 < b <= 2}.</p>
 *
 * @param temptation temptation to defect ({@code b})
 */
public record WeakPrisonersDilemma(double temptation) {

    /**
     * Creates a weak Prisoner's Dilemma.
     */
    public WeakPrisonersDilemma {
        if (!Double.isFinite(temptation) || temptation <= 1.0 || temptation > 2.0) {
            throw new IllegalArgumentException("temptation must be finite and in (1, 2]");
        }
    }

    /**
     * Returns the focal player's payoff for one interaction.
     *
     * @param focal focal player's strategy
     * @param opponent opponent's strategy
     * @return interaction payoff
     */
    public double payoff(Strategy focal, Strategy opponent) {
        if (opponent == Strategy.DEFECT) {
            return 0.0;
        }
        return focal == Strategy.COOPERATE ? 1.0 : temptation;
    }

    double accumulatedPayoff(byte focal, int cooperativeOpponents) {
        return focal == Strategy.COOPERATE.code()
                ? cooperativeOpponents
                : cooperativeOpponents * temptation;
    }
}
