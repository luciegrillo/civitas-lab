package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SeedDerivationTest {

    @Test
    void legacyInitializationSeedRemainsStable() {
        long seed = SeedDerivation.derive(19920359L, "frequency-baseline", 0);

        assertEquals(0x18991b5140c6c50eL, seed);
        assertEquals(seed, SeedDerivation.derive(19920359L, "frequency-baseline", 0));
        assertNotEquals(seed, SeedDerivation.derive(19920359L, "frequency-baseline", 1));
        assertNotEquals(seed, SeedDerivation.derive(19920359L, "frequency-variant", 0));
    }

    @Test
    void separatesNamedRandomnessDomains() {
        long initialization = SeedDerivation.derive(19920359L, "frequency-baseline", 0);
        long schedule = SeedDerivation.derive(
                19920359L, "schedule", "frequency-baseline", 0);

        assertEquals(0x1bab63efcd6570a0L, schedule);
        assertNotEquals(initialization, schedule);
        assertNotEquals(
                schedule,
                SeedDerivation.derive(19920359L, "schedule", "frequency-baseline", 1));
        assertNotEquals(
                schedule,
                SeedDerivation.derive(19920359L, "measurement", "frequency-baseline", 0));
    }

    @Test
    void rejectsInvalidStreamIdentity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SeedDerivation.derive(1, " ", 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> SeedDerivation.derive(1, " ", "group", 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> SeedDerivation.derive(1, "schedule", " ", 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> SeedDerivation.derive(1, "group", -1));
    }
}
