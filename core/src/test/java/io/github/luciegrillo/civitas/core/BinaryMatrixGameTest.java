package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BinaryMatrixGameTest {

    @Test
    void appliesConfiguredPayoffMatrix() {
        BinaryMatrixGame game = new BinaryMatrixGame(
                "test-game",
                3.0,
                0.5,
                4.0,
                1.0);

        assertEquals(3.0, game.payoff(Strategy.COOPERATE, Strategy.COOPERATE));
        assertEquals(0.5, game.payoff(Strategy.COOPERATE, Strategy.DEFECT));
        assertEquals(4.0, game.payoff(Strategy.DEFECT, Strategy.COOPERATE));
        assertEquals(1.0, game.payoff(Strategy.DEFECT, Strategy.DEFECT));
    }

    @Test
    void rejectsInvalidNamesAndPayoffs() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BinaryMatrixGame("Bad Name", 1.0, 0.0, 2.0, 0.0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new BinaryMatrixGame("bad-payoff", 1.0, 0.0, Double.NaN, 0.0));
    }
}
