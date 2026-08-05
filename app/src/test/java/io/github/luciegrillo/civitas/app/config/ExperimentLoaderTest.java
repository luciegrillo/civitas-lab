package io.github.luciegrillo.civitas.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExperimentLoaderTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadsSchema01WithImplicitSynchronousUpdates() throws IOException {
        ExperimentSpec experiment = new ExperimentLoader().load(write(validJson01()));

        ScenarioSpec scenario = experiment.scenarios().getFirst();
        assertEquals(ExperimentSpec.SCHEMA_VERSION_0_1, experiment.schemaVersion());
        assertEquals("smoke", experiment.experimentId());
        assertEquals(2, experiment.runCount());
        assertEquals(InitializationType.BERNOULLI, scenario.initialization().type());
        assertNull(scenario.updateSchedule());
        assertEquals(
                UpdateScheduleType.SYNCHRONOUS,
                scenario.effectiveUpdateSchedule().type());
    }

    @Test
    void loadsSchema02WithRandomSequentialUpdates() throws IOException {
        ExperimentSpec experiment = new ExperimentLoader().load(write(validJson02()));

        ScenarioSpec scenario = experiment.scenarios().getFirst();
        assertEquals(ExperimentSpec.CURRENT_SCHEMA_VERSION, experiment.schemaVersion());
        assertEquals(
                UpdateScheduleType.RANDOM_SEQUENTIAL,
                scenario.effectiveUpdateSchedule().type());
    }

    @Test
    void rejectsUnknownProperties() throws IOException {
        Path input = write(validJson01().replace(
                "\"parallelism\": 2,",
                "\"parallelism\": 2,\n  \"mystery\": true,"));

        IOException exception = assertThrows(
                IOException.class, () -> new ExperimentLoader().load(input));

        assertTrue(exception.getMessage().contains("mystery"));
    }

    @Test
    void rejectsMissingRequiredProperties() throws IOException {
        Path input = write(validJson01().replace("\"ticks\": 5,", ""));

        assertThrows(IOException.class, () -> new ExperimentLoader().load(input));
    }

    @Test
    void rejectsInvalidScientificParameters() throws IOException {
        Path input = write(validJson01().replace("[1.15]", "[2.5]"));

        IOException exception = assertThrows(
                IOException.class, () -> new ExperimentLoader().load(input));

        assertTrue(exception.getMessage().contains("temptation"));
    }

    @Test
    void rejectsMissingScheduleFromSchema02() throws IOException {
        Path input = write(validJson02().replace(
                "      \"updateSchedule\": {\n"
                        + "        \"type\": \"RANDOM_SEQUENTIAL\"\n"
                        + "      },\n",
                ""));

        IOException exception = assertThrows(
                IOException.class, () -> new ExperimentLoader().load(input));

        assertTrue(exception.getMessage().contains("updateSchedule"));
    }

    @Test
    void rejectsScheduleFromSchema01() throws IOException {
        Path input = write(validJson01().replace(
                "\"selfInteraction\": true,",
                "\"selfInteraction\": true,\n"
                        + "      \"updateSchedule\": {\n"
                        + "        \"type\": \"SYNCHRONOUS\"\n"
                        + "      },"));

        IOException exception = assertThrows(
                IOException.class, () -> new ExperimentLoader().load(input));

        assertTrue(exception.getMessage().contains("schemaVersion 0.1"));
    }

    @Test
    void rejectsUnsupportedSchemaVersion() throws IOException {
        Path input = write(validJson01().replace(
                "\"schemaVersion\": \"0.1\"",
                "\"schemaVersion\": \"9.9\""));

        IOException exception = assertThrows(
                IOException.class, () -> new ExperimentLoader().load(input));

        assertTrue(exception.getMessage().contains("unsupported schemaVersion"));
    }

    private Path write(String json) throws IOException {
        Path path = temporaryDirectory.resolve("experiment.json");
        Files.writeString(path, json);
        return path;
    }

    private static String validJson01() {
        return """
                {
                  "schemaVersion": "0.1",
                  "experimentId": "smoke",
                  "masterSeed": 42,
                  "parallelism": 2,
                  "scenarios": [
                    {
                      "id": "baseline",
                      "seedGroup": "paired",
                      "lattice": {
                        "width": 9,
                        "height": 9,
                        "boundary": "TOROIDAL"
                      },
                      "initialization": {
                        "type": "BERNOULLI",
                        "pCooperator": 0.9
                      },
                      "selfInteraction": true,
                      "temptationValues": [1.15],
                      "replicates": 2,
                      "ticks": 5,
                      "measurementStart": 2,
                      "snapshotTicks": [0, 5]
                    }
                  ]
                }
                """;
    }

    private static String validJson02() {
        return """
                {
                  "schemaVersion": "0.2",
                  "experimentId": "smoke",
                  "masterSeed": 42,
                  "parallelism": 2,
                  "scenarios": [
                    {
                      "id": "baseline",
                      "seedGroup": "paired",
                      "lattice": {
                        "width": 9,
                        "height": 9,
                        "boundary": "TOROIDAL"
                      },
                      "initialization": {
                        "type": "BERNOULLI",
                        "pCooperator": 0.9
                      },
                      "selfInteraction": true,
                      "updateSchedule": {
                        "type": "RANDOM_SEQUENTIAL"
                      },
                      "temptationValues": [1.15],
                      "replicates": 2,
                      "ticks": 5,
                      "measurementStart": 2,
                      "snapshotTicks": [0, 5]
                    }
                  ]
                }
                """;
    }
}
