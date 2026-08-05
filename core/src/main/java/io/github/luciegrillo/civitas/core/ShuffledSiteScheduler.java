package io.github.luciegrillo.civitas.core;

import java.util.Objects;

/**
 * Produces stable uniformly shuffled site orders without replacement.
 */
public final class ShuffledSiteScheduler {
    private final int siteCount;
    private final SplitMix64 random;

    /**
     * Creates a scheduler for a fixed population.
     *
     * @param siteCount number of sites in each sweep
     * @param seed schedule random seed
     */
    public ShuffledSiteScheduler(int siteCount, long seed) {
        if (siteCount < 1) {
            throw new IllegalArgumentException("siteCount must be positive");
        }
        this.siteCount = siteCount;
        random = new SplitMix64(seed);
    }

    /**
     * Fills {@code target} with the next Fisher-Yates shuffled sweep.
     *
     * @param target array whose length equals the configured site count
     */
    public void fillNextSweep(int[] target) {
        Objects.requireNonNull(target, "target");
        if (target.length != siteCount) {
            throw new IllegalArgumentException(
                    "target length does not match scheduler site count");
        }

        for (int site = 0; site < siteCount; site++) {
            target[site] = site;
        }
        for (int index = siteCount - 1; index > 0; index--) {
            int swapIndex = random.nextInt(index + 1);
            int temporary = target[index];
            target[index] = target[swapIndex];
            target[swapIndex] = temporary;
        }
    }
}
