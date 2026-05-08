package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LatticeTest {

    @Test
    void defensivelyCopiesInputAndOutputArrays() {
        byte[] input = new byte[9];
        Lattice lattice = Lattice.fromCodes(3, 3, input);

        input[0] = Strategy.DEFECT.code();
        byte[] output = lattice.copyCodes();
        output[1] = Strategy.DEFECT.code();

        assertEquals(Strategy.COOPERATE, lattice.strategyAt(0, 0));
        assertEquals(Strategy.COOPERATE, lattice.strategyAt(1, 0));
    }

    @Test
    void equalityIncludesDimensionsAndCells() {
        Lattice first = Lattice.filled(3, 3, Strategy.COOPERATE);
        Lattice same = Lattice.filled(3, 3, Strategy.COOPERATE);
        Lattice different = Lattice.filled(3, 3, Strategy.DEFECT);

        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, different);
    }

    @Test
    void rejectsUnknownStrategyCodes() {
        byte[] cells = new byte[9];
        cells[4] = 2;

        assertThrows(IllegalArgumentException.class, () -> Lattice.fromCodes(3, 3, cells));
    }
}
