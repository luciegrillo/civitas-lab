package io.github.luciegrillo.civitas.app.config;

import io.github.luciegrillo.civitas.app.json.JsonSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

/**
 * Loads and validates experiment definitions.
 */
public final class ExperimentLoader {

    /**
     * Reads a strict experiment JSON document.
     *
     * @param path input JSON
     * @return validated experiment
     * @throws IOException if the file cannot be read
     */
    public ExperimentSpec load(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        if (!Files.isRegularFile(path)) {
            throw new IOException("experiment file does not exist: " + path);
        }
        try {
            JsonNode document = JsonSupport.mapper().readTree(path);
            requireShape(document);
            return JsonSupport.mapper().treeToValue(document, ExperimentSpec.class);
        } catch (JacksonException | IllegalArgumentException exception) {
            throw new IOException(
                    "invalid experiment configuration: " + rootMessage(exception),
                    exception);
        }
    }

    private static String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage();
    }

    private static void requireShape(JsonNode document) {
        requireObject(document, "$");
        requireProperties(
                document,
                "$",
                "schemaVersion",
                "experimentId",
                "masterSeed",
                "parallelism",
                "scenarios");
        JsonNode scenarios = document.get("scenarios");
        if (scenarios == null || !scenarios.isArray()) {
            throw new IllegalArgumentException("$.scenarios must be an array");
        }
        for (int index = 0; index < scenarios.size(); index++) {
            JsonNode scenario = scenarios.get(index);
            String path = "$.scenarios[" + index + "]";
            requireObject(scenario, path);
            requireProperties(
                    scenario,
                    path,
                    "id",
                    "seedGroup",
                    "lattice",
                    "initialization",
                    "selfInteraction",
                    "temptationValues",
                    "replicates",
                    "ticks",
                    "measurementStart",
                    "snapshotTicks");

            JsonNode lattice = scenario.get("lattice");
            requireObject(lattice, path + ".lattice");
            requireProperties(lattice, path + ".lattice", "width", "height", "boundary");

            JsonNode initialization = scenario.get("initialization");
            requireObject(initialization, path + ".initialization");
            requireProperties(initialization, path + ".initialization", "type");
            JsonNode type = initialization.get("type");
            if (type != null
                    && type.isString()
                    && "BERNOULLI".equals(type.stringValue())
                    && !initialization.has("pCooperator")) {
                throw new IllegalArgumentException(
                        path + ".initialization is missing pCooperator");
            }
        }
    }

    private static void requireObject(JsonNode node, String path) {
        if (node == null || !node.isObject()) {
            throw new IllegalArgumentException(path + " must be an object");
        }
    }

    private static void requireProperties(JsonNode node, String path, String... properties) {
        for (String property : properties) {
            if (!node.has(property)) {
                throw new IllegalArgumentException(path + " is missing " + property);
            }
        }
    }
}
