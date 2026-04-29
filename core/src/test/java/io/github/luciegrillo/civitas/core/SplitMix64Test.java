package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SplitMix64Test {

    @Test
    void matchesPublishedReferenceSequence() {
        SplitMix64 random = new SplitMix64(0);

        assertEquals(0xe220a8397b1dcdafL, random.nextLong());
        assertEquals(0x6e789e6aa1b965f4L, random.nextLong());
        assertEquals(0x06c45d188009454fL, random.nextLong());
        assertEquals(0xf88bb8a8724c81ecL, random.nextLong());
        assertEquals(0x1b39896a51a8749bL, random.nextLong());
    }

    @Test
    void nextDoubleUsesHalfOpenUnitInterval() {
        SplitMix64 random = new SplitMix64(123);
        for (int index = 0; index < 10_000; index++) {
            double value = random.nextDouble();
            assertTrue(value >= 0.0);
            assertTrue(value < 1.0);
        }
    }
}
