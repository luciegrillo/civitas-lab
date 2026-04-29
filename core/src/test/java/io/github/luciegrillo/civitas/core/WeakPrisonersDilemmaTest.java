package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WeakPrisonersDilemmaTest {

    @Test
    void appliesWeakPrisonersDilemmaPayoffs() {
        WeakPrisonersDilemma game = new WeakPrisonersDilemma(1.85);

        assertEquals(1.0, game.payoff(Strategy.COOPERATE, Strategy.COOPERATE));
        assertEquals(0.0, game.payoff(Strategy.COOPERATE, Strategy.DEFECT));
        assertEquals(1.85, game.payoff(Strategy.DEFECT, Strategy.COOPERATE));
        assertEquals(0.0, game.payoff(Strategy.DEFECT, Strategy.DEFECT));
    }

    @Test
    void rejectsValuesOutsideCanonicalInterval() {
        assertThrows(IllegalArgumentException.class, () -> new WeakPrisonersDilemma(1.0));
        assertThrows(IllegalArgumentException.class, () -> new WeakPrisonersDilemma(2.01));
        assertThrows(
                IllegalArgumentException.class,
                () -> new WeakPrisonersDilemma(Double.NaN));
    }
}
