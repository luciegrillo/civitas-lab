package io.github.luciegrillo.civitas.core;

/**
 * Reproducible initial-condition factories.
 */
public final class LatticeInitializers {
    private LatticeInitializers() {
    }

    /**
     * Creates an all-cooperator lattice with one defector at the unique center.
     *
     * @param width odd lattice width
     * @param height odd lattice height
     * @return initialized lattice
     */
    public static Lattice centralDefector(int width, int height) {
        requirePositiveDimensions(width, height);
        if ((width & 1) == 0 || (height & 1) == 0) {
            throw new IllegalArgumentException(
                    "central-defector initialization requires odd dimensions");
        }
        byte[] cells = new byte[Math.multiplyExact(width, height)];
        cells[(height / 2) * width + width / 2] = Strategy.DEFECT.code();
        return Lattice.fromCodes(width, height, cells);
    }

    /**
     * Assigns cooperation independently to each site with probability
     * {@code pCooperator}.
     *
     * @param width lattice width
     * @param height lattice height
     * @param pCooperator probability of cooperation
     * @param seed random seed
     * @return initialized lattice
     */
    public static Lattice bernoulliCooperators(
            int width, int height, double pCooperator, long seed) {
        requirePositiveDimensions(width, height);
        if (!Double.isFinite(pCooperator) || pCooperator < 0.0 || pCooperator > 1.0) {
            throw new IllegalArgumentException("pCooperator must be finite and in [0, 1]");
        }

        SplitMix64 random = new SplitMix64(seed);
        byte[] cells = new byte[Math.multiplyExact(width, height)];
        for (int index = 0; index < cells.length; index++) {
            cells[index] = random.nextDouble() < pCooperator
                    ? Strategy.COOPERATE.code()
                    : Strategy.DEFECT.code();
        }
        return Lattice.fromCodes(width, height, cells);
    }

    private static void requirePositiveDimensions(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("width and height must be positive");
        }
    }
}
