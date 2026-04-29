package io.github.luciegrillo.civitas.core;

/**
 * Stable SplitMix64 pseudo-random number generator.
 *
 * <p>This implementation is deliberately owned by Civitas Lab so a run does
 * not change when a JDK provider changes its random-generator implementation.</p>
 */
public final class SplitMix64 {
    private static final long GOLDEN_GAMMA = 0x9e3779b97f4a7c15L;
    private static final long MIX_MULTIPLIER_1 = 0xbf58476d1ce4e5b9L;
    private static final long MIX_MULTIPLIER_2 = 0x94d049bb133111ebL;

    private long state;

    /**
     * Creates a generator at the supplied state.
     *
     * @param seed initial state
     */
    public SplitMix64(long seed) {
        state = seed;
    }

    /**
     * Produces the next 64 random bits.
     *
     * @return next pseudo-random value
     */
    public long nextLong() {
        state += GOLDEN_GAMMA;
        return mix(state);
    }

    /**
     * Produces a value in the half-open interval {@code [0, 1)}.
     *
     * @return uniformly distributed double with 53 random bits
     */
    public double nextDouble() {
        return (nextLong() >>> 11) * 0x1.0p-53;
    }

    static long mix(long value) {
        long mixed = value;
        mixed = (mixed ^ (mixed >>> 30)) * MIX_MULTIPLIER_1;
        mixed = (mixed ^ (mixed >>> 27)) * MIX_MULTIPLIER_2;
        return mixed ^ (mixed >>> 31);
    }
}
