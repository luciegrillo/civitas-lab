package io.github.luciegrillo.civitas.app.artifact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.luciegrillo.civitas.core.Strategy;
import org.junit.jupiter.api.Test;

class SnapshotRendererTest {

    @Test
    void mapsAllFourTransitionsToHistoricalPalette() {
        assertEquals(
                SnapshotRenderer.STABLE_COOPERATOR,
                SnapshotRenderer.color(Strategy.COOPERATE, Strategy.COOPERATE));
        assertEquals(
                SnapshotRenderer.STABLE_DEFECTOR,
                SnapshotRenderer.color(Strategy.DEFECT, Strategy.DEFECT));
        assertEquals(
                SnapshotRenderer.BECAME_DEFECTOR,
                SnapshotRenderer.color(Strategy.COOPERATE, Strategy.DEFECT));
        assertEquals(
                SnapshotRenderer.BECAME_COOPERATOR,
                SnapshotRenderer.color(Strategy.DEFECT, Strategy.COOPERATE));
    }
}
