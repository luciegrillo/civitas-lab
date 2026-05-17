package io.github.luciegrillo.civitas.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

class CliIntegrationTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void runsThenValidatesAnExperiment() throws IOException {
        Path config = temporaryDirectory.resolve("experiment.json");
        Path output = temporaryDirectory.resolve("output");
        Files.writeString(config, """
                {
                  "schemaVersion": "0.1",
                  "experimentId": "cli-smoke",
                  "masterSeed": 7,
                  "parallelism": 1,
                  "scenarios": [
                    {
                      "id": "central",
                      "seedGroup": "central",
                      "lattice": {
                        "width": 5,
                        "height": 5,
                        "boundary": "BOUNDED"
                      },
                      "initialization": {
                        "type": "CENTRAL_DEFECTOR"
                      },
                      "selfInteraction": true,
                      "temptationValues": [1.85],
                      "replicates": 1,
                      "ticks": 2,
                      "measurementStart": 0,
                      "snapshotTicks": [0, 2]
                    }
                  ]
                }
                """);

        CommandLine commandLine = commandLine();
        assertEquals(
                0,
                commandLine.execute(
                        "run",
                        config.toString(),
                        "--output",
                        output.toString()));
        assertEquals(0, commandLine.execute("validate", output.toString()));
        assertTrue(Files.isRegularFile(output.resolve("figures/central-timeseries.png")));
    }

    @Test
    void invalidConfigurationReturnsFailure() throws IOException {
        Path config = temporaryDirectory.resolve("invalid.json");
        Files.writeString(config, "{}");

        assertEquals(
                1,
                commandLine().execute(
                        "run",
                        config.toString(),
                        "--output",
                        temporaryDirectory.resolve("output").toString()));
    }

    private static CommandLine commandLine() {
        CommandLine commandLine = new CommandLine(new CivitasApplication());
        commandLine.setOut(new PrintWriter(
                new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
        commandLine.setErr(new PrintWriter(
                new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
        return commandLine;
    }
}
