package io.github.luciegrillo.civitas.core;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Derives stable, independent run seeds from an experiment master seed.
 */
public final class SeedDerivation {
    private static final long FNV_OFFSET_BASIS = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;

    private SeedDerivation() {
    }

    /**
     * Derives a run seed. Reusing the same seed group and replicate index pairs
     * initial conditions across different model parameters.
     *
     * @param masterSeed experiment-level seed
     * @param seedGroup stable logical group name
     * @param replicateIndex zero-based replicate index
     * @return deterministic run seed
     */
    public static long derive(long masterSeed, String seedGroup, int replicateIndex) {
        Objects.requireNonNull(seedGroup, "seedGroup");
        if (seedGroup.isBlank()) {
            throw new IllegalArgumentException("seedGroup must not be blank");
        }
        if (replicateIndex < 0) {
            throw new IllegalArgumentException("replicateIndex must not be negative");
        }

        long groupHash = FNV_OFFSET_BASIS;
        for (byte value : seedGroup.getBytes(StandardCharsets.UTF_8)) {
            groupHash ^= Byte.toUnsignedLong(value);
            groupHash *= FNV_PRIME;
        }
        long stream = groupHash ^ Integer.toUnsignedLong(replicateIndex);
        return SplitMix64.mix(masterSeed + SplitMix64.mix(stream));
    }
}
