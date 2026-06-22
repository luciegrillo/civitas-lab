package io.github.luciegrillo.civitas.core;

import java.util.Objects;

/**
 * Immutable payoff matrix for a two-strategy game.
 *
 * @param name stable model identifier
 * @param cooperateAgainstCooperate payoff for C against C
 * @param cooperateAgainstDefect payoff for C against D
 * @param defectAgainstCooperate payoff for D against C
 * @param defectAgainstDefect payoff for D against D
 */
public record BinaryMatrixGame(
        String name,
        double cooperateAgainstCooperate,
        double cooperateAgainstDefect,
        double defectAgainstCooperate,
        double defectAgainstDefect) implements BinaryPayoff {

    /**
     * Creates a validated two-strategy payoff matrix.
     */
    public BinaryMatrixGame {
        name = Objects.requireNonNull(name, "name").strip();
        if (!name.matches("[a-z0-9][a-z0-9-]*")) {
            throw new IllegalArgumentException("name must match [a-z0-9][a-z0-9-]*");
        }
        requireFinite(cooperateAgainstCooperate, "cooperateAgainstCooperate");
        requireFinite(cooperateAgainstDefect, "cooperateAgainstDefect");
        requireFinite(defectAgainstCooperate, "defectAgainstCooperate");
        requireFinite(defectAgainstDefect, "defectAgainstDefect");
    }

    @Override
    public double payoff(Strategy focal, Strategy opponent) {
        Objects.requireNonNull(focal, "focal");
        Objects.requireNonNull(opponent, "opponent");
        if (focal == Strategy.COOPERATE) {
            return opponent == Strategy.COOPERATE
                    ? cooperateAgainstCooperate
                    : cooperateAgainstDefect;
        }
        return opponent == Strategy.COOPERATE
                ? defectAgainstCooperate
                : defectAgainstDefect;
    }

    private static void requireFinite(double value, String label) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(label + " must be finite");
        }
    }
}
