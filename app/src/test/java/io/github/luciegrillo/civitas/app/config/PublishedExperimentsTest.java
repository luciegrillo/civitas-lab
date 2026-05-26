package io.github.luciegrillo.civitas.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.luciegrillo.civitas.core.BoundaryCondition;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PublishedExperimentsTest {
    private final Path repositoryRoot =
            Path.of(System.getProperty("civitas.repoRoot"));
    private final ExperimentLoader loader = new ExperimentLoader();

    @Test
    void frequencyProfileMatchesDocumentedHistoricalSetup() throws IOException {
        ExperimentSpec experiment = loader.load(
                repositoryRoot.resolve("configs/nowak-may-frequency.json"));
        ScenarioSpec scenario = experiment.scenarios().getFirst();

        assertEquals(12, experiment.runCount());
        assertEquals(20, scenario.lattice().width());
        assertEquals(20, scenario.lattice().height());
        assertEquals(BoundaryCondition.TOROIDAL, scenario.lattice().boundary());
        assertEquals(0.9, scenario.initialization().pCooperator());
        assertEquals(200, scenario.ticks());
    }

    @Test
    void kaleidoscopeMatchesDocumentedHistoricalSetup() throws IOException {
        ExperimentSpec experiment = loader.load(
                repositoryRoot.resolve("configs/nowak-may-kaleidoscope.json"));
        ScenarioSpec scenario = experiment.scenarios().getFirst();

        assertEquals(1, experiment.runCount());
        assertEquals(49, scenario.lattice().width());
        assertEquals(49, scenario.lattice().height());
        assertEquals(BoundaryCondition.BOUNDED, scenario.lattice().boundary());
        assertEquals(InitializationType.CENTRAL_DEFECTOR, scenario.initialization().type());
        assertEquals(179, scenario.ticks());
    }
}
