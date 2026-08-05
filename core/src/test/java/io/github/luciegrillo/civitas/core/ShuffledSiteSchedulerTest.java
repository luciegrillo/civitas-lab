package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ShuffledSiteSchedulerTest {

    @Test
    void producesStableSuccessiveSweeps() {
        ShuffledSiteScheduler scheduler = new ShuffledSiteScheduler(10, 0);
        int[] order = new int[10];

        scheduler.fillNextSweep(order);
        assertArrayEquals(new int[] {5, 6, 1, 4, 8, 3, 2, 9, 0, 7}, order);

        scheduler.fillNextSweep(order);
        assertArrayEquals(new int[] {6, 2, 4, 9, 7, 1, 8, 3, 0, 5}, order);
    }

    @Test
    void eachSweepVisitsEverySiteExactlyOnce() {
        ShuffledSiteScheduler scheduler = new ShuffledSiteScheduler(257, 42);
        int[] order = new int[257];

        for (int sweep = 0; sweep < 100; sweep++) {
            scheduler.fillNextSweep(order);
            int[] sorted = order.clone();
            Arrays.sort(sorted);
            for (int site = 0; site < sorted.length; site++) {
                if (sorted[site] != site) {
                    throw new AssertionError("invalid shuffled sweep at site " + site);
                }
            }
        }
    }

    @Test
    void rejectsInvalidPopulationAndTargetLength() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ShuffledSiteScheduler(0, 0));

        ShuffledSiteScheduler scheduler = new ShuffledSiteScheduler(4, 0);
        assertThrows(NullPointerException.class, () -> scheduler.fillNextSweep(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> scheduler.fillNextSweep(new int[3]));
    }
}
