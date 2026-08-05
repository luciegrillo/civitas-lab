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
     * Derives the legacy run seed used for initialization in schema 0.1.
     * Reusing the same seed group and replicate index pairs initial conditions
     * across different model parameters.
     *
     * @param masterSeed experiment-level seed
     * @param seedGroup stable logical group name
     * @param replicateIndex zero-based replicate index
     * @return deterministic initialization seed
     */
    public static long derive(long masterSeed, String seedGroup, int replicateIndex) {
        validateIdentity(seedGroup, "seedGroup");
        validateReplicate(replicateIndex);
        return deriveFromHash(masterSeed, hash(FNV_OFFSET_BASIS, seedGroup), replicateIndex);
    }

    /**
     * Derives a seed in an explicit logical domain. Domain separation prevents
     * schedule randomness from sharing the initialization stream while keeping
     * both streams stable across Java versions.
     *
     * @param masterSeed experiment-level seed
     * @param domain stable domain identifier such as {@code schedule}
     * @param seedGroup stable logical group name
     * @param replicateIndex zero-based replicate index
     * @return deterministic domain-specific seed
     */
    public static long derive(
            long masterSeed, String domain, String seedGroup, int replicateIndex) {
        validateIdentity(domain, "domain");
        validateIdentity(seedGroup, "seedGroup");
        validateReplicate(replicateIndex);

        long streamHash = hash(FNV_OFFSET_BASIS, domain);
        streamHash = updateHash(streamHash, 0);
        streamHash = hash(streamHash, seedGroup);
        return deriveFromHash(masterSeed, streamHash, replicateIndex);
    }

    private static long deriveFromHash(
            long masterSeed, long streamHash, int replicateIndex) {
        long stream = streamHash ^ Integer.toUnsignedLong(replicateIndex);
        return SplitMix64.mix(masterSeed + SplitMix64.mix(stream));
    }

    private static long hash(long initial, String value) {
        long hash = initial;
        for (byte item : value.getBytes(StandardCharsets.UTF_8)) {
            hash = updateHash(hash, Byte.toUnsignedLong(item));
        }
        return hash;
    }

    private static long updateHash(long hash, long value) {
        return (hash ^ value) * FNV_PRIME;
    }

    private static void validateIdentity(String value, String label) {
        Objects.requireNonNull(value, label);
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
    }

    private static void validateReplicate(int replicateIndex) {
        if (replicateIndex < 0) {
            throw new IllegalArgumentException("replicateIndex must not be negative");
        }
    }
}
