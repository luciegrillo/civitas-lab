package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LatticeInitializersTest {

    @Test
    void placesOneDefectorAtUniqueCenter() {
        Lattice lattice = LatticeInitializers.centralDefector(5, 7);

        assertEquals(34, lattice.count(Strategy.COOPERATE));
        assertEquals(1, lattice.count(Strategy.DEFECT));
        assertEquals(Strategy.DEFECT, lattice.strategyAt(2, 3));
    }

    @Test
    void requiresOddDimensionsForCentralDefector() {
        assertThrows(
                IllegalArgumentException.class,
                () -> LatticeInitializers.centralDefector(4, 5));
    }

    @Test
    void bernoulliInitializationIsStableForASeed() {
        Lattice first = LatticeInitializers.bernoulliCooperators(10, 10, 0.7, 42);
        Lattice second = LatticeInitializers.bernoulliCooperators(10, 10, 0.7, 42);
        Lattice anotherSeed = LatticeInitializers.bernoulliCooperators(10, 10, 0.7, 43);

        assertEquals(first, second);
        org.junit.jupiter.api.Assertions.assertNotEquals(first, anotherSeed);
    }

    @Test
    void probabilityExtremesProduceUniformLattices() {
        assertEquals(
                25,
                LatticeInitializers.bernoulliCooperators(5, 5, 1.0, 7)
                        .count(Strategy.COOPERATE));
        assertEquals(
                25,
                LatticeInitializers.bernoulliCooperators(5, 5, 0.0, 7)
                        .count(Strategy.DEFECT));
    }
}
