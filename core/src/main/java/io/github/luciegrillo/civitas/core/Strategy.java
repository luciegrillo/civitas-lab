package io.github.luciegrillo.civitas.core;

/**
 * A pure strategy in the two-player Prisoner's Dilemma.
 */
public enum Strategy {
    /** Cooperates with the interaction partner. */
    COOPERATE((byte) 0),

    /** Defects against the interaction partner. */
    DEFECT((byte) 1);

    private final byte code;

    Strategy(byte code) {
        this.code = code;
    }

    byte code() {
        return code;
    }

    static Strategy fromCode(byte code) {
        return switch (code) {
            case 0 -> COOPERATE;
            case 1 -> DEFECT;
            default -> throw new IllegalArgumentException("Unknown strategy code: " + code);
        };
    }
}
