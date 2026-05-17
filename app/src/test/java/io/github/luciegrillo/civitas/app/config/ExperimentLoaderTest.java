package io.github.luciegrillo.civitas.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void loadsACompleteStrictDocument() throws IOException {
        Path input = write(validJson());

        ExperimentSpec experiment = new ExperimentLoader().load(input);

        assertEquals("smoke", experiment.experimentId());
        assertEquals(2, experiment.runCount());
        assertEquals(InitializationType.BERNOULLI,
                experiment.scenarios().getFirst().initialization().type());
    }

    @Test
    void rejectsUnknownProperties() throws IOException {
        Path input = write(validJson().replace(
                "\"parallelism\": 2,",
                "\"parallelism\": 2,\n  \"mystery\": true,"));

        IOException exception = assertThrows(
                IOException.class, () -> new ExperimentLoader().load(input));

        assertTrue(exception.getMessage().contains("mystery"));
    }

    @Test
    void rejectsMissingRequiredProperties() throws IOException {
        Path input = write(validJson().replace("\"ticks\": 5,", ""));

        assertThrows(IOException.class, () -> new ExperimentLoader().load(input));
    }

    @Test
    void rejectsInvalidScientificParameters() throws IOException {
        Path input = write(validJson().replace("[1.15]", "[2.5]"));

        IOException exception = assertThrows(
                IOException.class, () -> new ExperimentLoader().load(input));

        assertTrue(exception.getMessage().contains("temptation"));
    }

    private Path write(String json) throws IOException {
        Path path = temporaryDirectory.resolve("experiment.json");
        Files.writeString(path, json);
        return path;
    }

    private static String validJson() {
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
}
