package io.github.luciegrillo.civitas.core;

/**
 * A pure strategy in a two-strategy spatial game.
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

    /**
     * Returns the compact lattice code.
     *
     * @return strategy code
     */
    public byte code() {
        return code;
    }

    /**
     * Decodes a compact lattice value.
     *
     * @param code strategy code
     * @return decoded strategy
     */
    public static Strategy fromCode(byte code) {
        return switch (code) {
            case 0 -> COOPERATE;
            case 1 -> DEFECT;
            default -> throw new IllegalArgumentException("Unknown strategy code: " + code);
        };
    }
}
