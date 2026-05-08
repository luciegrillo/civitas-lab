package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SimulationConfigTest {

    @Test
    void computesSiteCountExactly() {
        SimulationConfig config = new SimulationConfig(
                20,
                30,
                BoundaryCondition.TOROIDAL,
                new WeakPrisonersDilemma(1.5),
                true);

        assertEquals(600, config.siteCount());
    }

    @Test
    void rejectsDimensionsThatDuplicateToroidalNeighbors() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new SimulationConfig(
                        2,
                        3,
                        BoundaryCondition.TOROIDAL,
                        new WeakPrisonersDilemma(1.5),
                        true));
    }
}
