package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SeedDerivationTest {

    @Test
    void isStableAndSeparatesLogicalStreams() {
        long seed = SeedDerivation.derive(19920359L, "frequency-baseline", 0);

        assertEquals(seed, SeedDerivation.derive(19920359L, "frequency-baseline", 0));
        assertNotEquals(seed, SeedDerivation.derive(19920359L, "frequency-baseline", 1));
        assertNotEquals(seed, SeedDerivation.derive(19920359L, "frequency-variant", 0));
    }

    @Test
    void rejectsInvalidStreamIdentity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SeedDerivation.derive(1, " ", 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> SeedDerivation.derive(1, "group", -1));
    }
}
