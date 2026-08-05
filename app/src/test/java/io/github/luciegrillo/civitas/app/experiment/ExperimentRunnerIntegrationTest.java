package io.github.luciegrillo.civitas.app.experiment;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.luciegrillo.civitas.app.artifact.ChecksumManifest;
import io.github.luciegrillo.civitas.app.config.ExperimentSpec;
import io.github.luciegrillo.civitas.app.config.InitializationSpec;
import io.github.luciegrillo.civitas.app.config.InitializationType;
import io.github.luciegrillo.civitas.app.config.LatticeSpec;
import io.github.luciegrillo.civitas.app.config.ScenarioSpec;
import io.github.luciegrillo.civitas.app.config.UpdateScheduleSpec;
import io.github.luciegrillo.civitas.app.config.UpdateScheduleType;
import io.github.luciegrillo.civitas.core.BoundaryCondition;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExperimentRunnerIntegrationTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsAndValidatesCompleteArtifacts() throws IOException {
        Path output = temporaryDirectory.resolve("artifacts");

        ExecutionReport report = new ExperimentRunner().run(experiment(2), output, false);

        assertEquals(output.toAbsolutePath(), report.outputDirectory());
        assertEquals(4, report.runCount());
        assertTrue(Files.isRegularFile(output.resolve("resolved-experiment.json")));
        assertTrue(Files.isRegularFile(output.resolve("provenance.json")));
        assertTrue(Files.isRegularFile(output.resolve("summary.csv")));
        assertTrue(Files.isRegularFile(output.resolve("aggregate.csv")));
        assertTrue(Files.isRegularFile(output.resolve("checksums.sha256")));
        assertEquals(0, ChecksumManifest.validate(output).size());
        assertEquals(
                4,
                Files.list(output.resolve("runs")).filter(Files::isDirectory).count());
        String metadata = Files.readString(output.resolve(
                "runs/baseline__b-1_15__r-000/metadata.json"));
        assertTrue(metadata.contains("\"type\" : \"SYNCHRONOUS\""));
        assertTrue(metadata.contains("\"scheduleSeed\""));
        assertFalse(hasWorkspaceSibling(".artifacts.staging-"));
        assertFalse(hasWorkspaceSibling(".artifacts.backup-"));
    }

    @Test
    void executesRandomSequentialScenarios() throws IOException {
        Path output = temporaryDirectory.resolve("random-sequential");

        ExecutionReport report = new ExperimentRunner().run(
                scheduleExperiment(UpdateScheduleType.RANDOM_SEQUENTIAL), output, false);

        assertEquals(1, report.runCount());
        assertEquals(0, ChecksumManifest.validate(output).size());
        String metadata = Files.readString(output.resolve(
                "runs/random-sequential__b-1_85__r-000/metadata.json"));
        assertTrue(metadata.contains("\"type\" : \"RANDOM_SEQUENTIAL\""));
        assertTrue(Files.isRegularFile(output.resolve(
                "runs/random-sequential__b-1_85__r-000/timeseries.csv")));
    }

    @Test
    void rejectsExistingOutputUnlessOverwriteIsExplicit() throws IOException {
        Path output = temporaryDirectory.resolve("artifacts");
        ExperimentRunner runner = new ExperimentRunner();
        runner.run(experiment(1), output, false);
        byte[] originalManifest = Files.readAllBytes(output.resolve("checksums.sha256"));

        assertThrows(IOException.class, () -> runner.run(experiment(1), output, false));
        assertArrayEquals(originalManifest, Files.readAllBytes(output.resolve("checksums.sha256")));

        runner.run(experiment(1), output, true);
        assertEquals(0, ChecksumManifest.validate(output).size());
        assertFalse(hasWorkspaceSibling(".artifacts.staging-"));
        assertFalse(hasWorkspaceSibling(".artifacts.backup-"));
    }

    @Test
    void scientificOutputsDoNotDependOnParallelism() throws IOException {
        Path serial = temporaryDirectory.resolve("serial");
        Path parallel = temporaryDirectory.resolve("parallel");

        new ExperimentRunner().run(experiment(1), serial, false);
        new ExperimentRunner().run(experiment(4), parallel, false);

        assertArrayEquals(
                Files.readAllBytes(serial.resolve("summary.csv")),
                Files.readAllBytes(parallel.resolve("summary.csv")));
        assertArrayEquals(
                Files.readAllBytes(serial.resolve("aggregate.csv")),
                Files.readAllBytes(parallel.resolve("aggregate.csv")));

        try (var paths = Files.walk(serial.resolve("runs"))) {
            for (Path first : paths.filter(Files::isRegularFile).toList()) {
                Path relative = serial.resolve("runs").relativize(first);
                Path second = parallel.resolve("runs").resolve(relative);
                assertArrayEquals(
                        Files.readAllBytes(first),
                        Files.readAllBytes(second),
                        relative.toString());
            }
        }
    }

    private boolean hasWorkspaceSibling(String prefix) throws IOException {
        try (var paths = Files.list(temporaryDirectory)) {
            return paths.anyMatch(path -> path.getFileName().toString().startsWith(prefix));
        }
    }

    private static ExperimentSpec experiment(int parallelism) {
        ScenarioSpec scenario = new ScenarioSpec(
                "baseline",
                "paired-initialization",
                new LatticeSpec(9, 9, BoundaryCondition.TOROIDAL),
                new InitializationSpec(InitializationType.BERNOULLI, 0.9),
                true,
                List.of(1.15, 1.85),
                2,
                10,
                5,
                List.of(0, 10));
        return new ExperimentSpec(
                ExperimentSpec.CURRENT_SCHEMA_VERSION,
                "integration-test",
                19920359L,
                parallelism,
                List.of(scenario));
    }

    private static ExperimentSpec scheduleExperiment(UpdateScheduleType type) {
        ScenarioSpec scenario = new ScenarioSpec(
                type.name().toLowerCase().replace('_', '-'),
                "paired-schedule",
                new LatticeSpec(9, 9, BoundaryCondition.TOROIDAL),
                new InitializationSpec(InitializationType.BERNOULLI, 0.9),
                true,
                List.of(1.85),
                1,
                10,
                5,
                List.of(0, 10),
                new UpdateScheduleSpec(type));
        return new ExperimentSpec(
                ExperimentSpec.CURRENT_SCHEMA_VERSION,
                "schedule-integration-test",
                19920359L,
                1,
                List.of(scenario));
    }
}
