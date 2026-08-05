package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RandomSequentialSpatialGameEngineTest {

    @Test
    void uniformStrategiesAreAbsorbingStates() {
        SimulationEngine cooperators = engine(
                Lattice.filled(5, 5, Strategy.COOPERATE), 1.85, 1);
        SimulationEngine defectors = engine(
                Lattice.filled(5, 5, Strategy.DEFECT), 1.85, 2);

        assertEquals(0, cooperators.step().strategyChanges());
        assertEquals(25, cooperators.metrics().cooperators());
        assertEquals(0, defectors.step().strategyChanges());
        assertEquals(25, defectors.metrics().defectors());
    }

    @Test
    void repeatedRunsAreBitIdentical() {
        Lattice initial = LatticeInitializers.bernoulliCooperators(20, 20, 0.9, 99);
        SimulationEngine first = engine(initial, 1.85, 1234);
        SimulationEngine second = engine(initial, 1.85, 1234);

        for (int generation = 0; generation < 200; generation++) {
            assertEquals(first.step(), second.step());
        }
        assertArrayEquals(
                first.snapshot().lattice().copyCodes(),
                second.snapshot().lattice().copyCodes());
    }

    @Test
    void scheduleSeedDoesNotChangeInitialStateButCanChangeTrajectory() {
        Lattice initial = Lattice.fromCodes(
                5,
                5,
                codes("1100001000100100111101100"));
        SimulationEngine first = engine(initial, 1.85, 1);
        SimulationEngine second = engine(initial, 1.85, 2);

        assertEquals(first.snapshot(), second.snapshot());

        first.step();
        second.step();

        assertEquals(0, first.metrics().cooperators());
        assertEquals(9, second.metrics().cooperators());
        assertNotEquals(first.snapshot(), second.snapshot());
    }

    @Test
    void rejectsMismatchedInitialDimensions() {
        SimulationConfig config = new SimulationConfig(
                5,
                5,
                BoundaryCondition.TOROIDAL,
                new WeakPrisonersDilemma(1.5),
                true);

        assertThrows(
                IllegalArgumentException.class,
                () -> new RandomSequentialSpatialGameEngine(
                        config,
                        Lattice.filled(3, 3, Strategy.COOPERATE),
                        0));
    }

    private static RandomSequentialSpatialGameEngine engine(
            Lattice initial, double temptation, long scheduleSeed) {
        SimulationConfig config = new SimulationConfig(
                initial.width(),
                initial.height(),
                BoundaryCondition.TOROIDAL,
                new WeakPrisonersDilemma(temptation),
                true);
        return new RandomSequentialSpatialGameEngine(config, initial, scheduleSeed);
    }

    private static byte[] codes(String values) {
        byte[] result = new byte[values.length()];
        for (int index = 0; index < values.length(); index++) {
            result[index] = switch (values.charAt(index)) {
                case '0' -> Strategy.COOPERATE.code();
                case '1' -> Strategy.DEFECT.code();
                default -> throw new IllegalArgumentException("unexpected strategy code");
            };
        }
        return result;
    }
}
