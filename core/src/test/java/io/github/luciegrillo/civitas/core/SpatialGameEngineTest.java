package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;

class SpatialGameEngineTest {

    @Test
    void uniformStrategiesAreAbsorbingStates() {
        SpatialGameEngine cooperators = engine(
                5, 5, BoundaryCondition.BOUNDED, 1.85, true,
                Lattice.filled(5, 5, Strategy.COOPERATE));
        SpatialGameEngine defectors = engine(
                5, 5, BoundaryCondition.TOROIDAL, 1.85, true,
                Lattice.filled(5, 5, Strategy.DEFECT));

        assertEquals(0, cooperators.step().strategyChanges());
        assertEquals(25, cooperators.metrics().cooperators());
        assertEquals(0, defectors.step().strategyChanges());
        assertEquals(25, defectors.metrics().defectors());
    }

    @Test
    void isolatedDefectorCrossesAnalyticalNineEighthsBreakpoint() {
        Lattice initial = LatticeInitializers.centralDefector(5, 5);
        SpatialGameEngine belowBreakpoint = engine(
                5, 5, BoundaryCondition.TOROIDAL, 1.10, true, initial);
        SpatialGameEngine aboveBreakpoint = engine(
                5, 5, BoundaryCondition.TOROIDAL, 1.20, true, initial);

        assertEquals(1, belowBreakpoint.step().defectors());
        assertEquals(9, aboveBreakpoint.step().defectors());
    }

    @Test
    void usesCurrentGenerationForEveryUpdateDecision() {
        Lattice initial = LatticeInitializers.centralDefector(5, 5);
        SpatialGameEngine engine = engine(
                5, 5, BoundaryCondition.TOROIDAL, 1.10, false, initial);

        engine.step();

        assertEquals(9, engine.metrics().defectors());
        assertEquals(16, engine.metrics().cooperators());
    }

    @Test
    void crossStrategyPayoffTieRetainsFocalStrategy() {
        assertEquals(
                Strategy.COOPERATE.code(),
                SpatialGameEngine.selectStrategy(Strategy.COOPERATE.code(), 5.0, 5.0));
        assertEquals(
                Strategy.DEFECT.code(),
                SpatialGameEngine.selectStrategy(Strategy.DEFECT.code(), 5.0, 5.0));
    }

    @Test
    void supportsCustomBinaryPayoffModels() {
        BinaryMatrixGame coordinationGame = new BinaryMatrixGame(
                "self-defect-test",
                1.0,
                0.0,
                0.0,
                10.0);
        SimulationConfig config = new SimulationConfig(
                5,
                5,
                BoundaryCondition.TOROIDAL,
                coordinationGame,
                true);
        SpatialGameEngine engine = new SpatialGameEngine(
                config, LatticeInitializers.centralDefector(5, 5));

        engine.step();

        assertEquals(9, engine.metrics().defectors());
    }

    @Test
    void repeatedRunsAreBitIdentical() {
        Lattice initial = LatticeInitializers.bernoulliCooperators(20, 20, 0.9, 99);
        SpatialGameEngine first = engine(
                20, 20, BoundaryCondition.TOROIDAL, 1.85, true, initial);
        SpatialGameEngine second = engine(
                20, 20, BoundaryCondition.TOROIDAL, 1.85, true, initial);

        for (int generation = 0; generation < 200; generation++) {
            assertEquals(first.step(), second.step());
        }
        assertArrayEquals(
                first.snapshot().lattice().copyCodes(),
                second.snapshot().lattice().copyCodes());
    }

    @Test
    void centralDefectorDynamicsRemainReflectionSymmetric() {
        int size = 49;
        SpatialGameEngine engine = engine(
                size,
                size,
                BoundaryCondition.BOUNDED,
                1.85,
                true,
                LatticeInitializers.centralDefector(size, size));

        for (int generation = 0; generation < 179; generation++) {
            engine.step();
        }

        Lattice lattice = engine.snapshot().lattice();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Strategy strategy = lattice.strategyAt(x, y);
                assertEquals(strategy, lattice.strategyAt(size - 1 - x, y));
                assertEquals(strategy, lattice.strategyAt(x, size - 1 - y));
            }
        }
    }

    @Test
    void kaleidoscopeStateHasStableInternalRegressionHash()
            throws NoSuchAlgorithmException {
        int size = 49;
        SpatialGameEngine engine = engine(
                size,
                size,
                BoundaryCondition.BOUNDED,
                1.85,
                true,
                LatticeInitializers.centralDefector(size, size));

        for (int generation = 0; generation < 179; generation++) {
            engine.step();
        }

        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(engine.snapshot().lattice().copyCodes());
        assertEquals(
                "3a58ca068761e6eca6fcd918cd08c2c4db467c5bac8f38e6d83d36b838a94df5",
                HexFormat.of().formatHex(digest));
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
                () -> new SpatialGameEngine(
                        config, Lattice.filled(3, 3, Strategy.COOPERATE)));
    }

    private static SpatialGameEngine engine(
            int width,
            int height,
            BoundaryCondition boundary,
            double temptation,
            boolean selfInteraction,
            Lattice initial) {
        SimulationConfig config = new SimulationConfig(
                width,
                height,
                boundary,
                new WeakPrisonersDilemma(temptation),
                selfInteraction);
        return new SpatialGameEngine(config, initial);
    }
}
