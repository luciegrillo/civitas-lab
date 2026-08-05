package io.github.luciegrillo.civitas.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Complete machine-readable experiment definition.
 */
public record ExperimentSpec(
        @JsonProperty(required = true) String schemaVersion,
        @JsonProperty(required = true) String experimentId,
        @JsonProperty(required = true) long masterSeed,
        @JsonProperty(required = true) int parallelism,
        @JsonProperty(required = true) List<ScenarioSpec> scenarios) {

    public static final String SCHEMA_VERSION_0_1 = "0.1";
    public static final String CURRENT_SCHEMA_VERSION = "0.2";
    private static final Set<String> SUPPORTED_SCHEMA_VERSIONS =
            Set.of(SCHEMA_VERSION_0_1, CURRENT_SCHEMA_VERSION);

    public ExperimentSpec {
        if (!SUPPORTED_SCHEMA_VERSIONS.contains(schemaVersion)) {
            throw new IllegalArgumentException(
                    "schemaVersion must be one of " + SUPPORTED_SCHEMA_VERSIONS);
        }
        Objects.requireNonNull(experimentId, "experimentId");
        experimentId = experimentId.strip();
        if (!experimentId.matches("[a-z0-9][a-z0-9-]*")) {
            throw new IllegalArgumentException(
                    "experimentId must match [a-z0-9][a-z0-9-]*");
        }
        if (parallelism < 1 || parallelism > 256) {
            throw new IllegalArgumentException("parallelism must be in [1, 256]");
        }
        scenarios = List.copyOf(Objects.requireNonNull(scenarios, "scenarios"));
        if (scenarios.isEmpty()) {
            throw new IllegalArgumentException("scenarios must not be empty");
        }
        HashSet<String> scenarioIds = new HashSet<>();
        for (ScenarioSpec scenario : scenarios) {
            Objects.requireNonNull(scenario, "scenario");
            if (!scenarioIds.add(scenario.id())) {
                throw new IllegalArgumentException(
                        "duplicate scenario id: " + scenario.id());
            }
            if (CURRENT_SCHEMA_VERSION.equals(schemaVersion)
                    && scenario.updateSchedule() == null) {
                throw new IllegalArgumentException(
                        "schemaVersion 0.2 requires updateSchedule for every scenario");
            }
            if (SCHEMA_VERSION_0_1.equals(schemaVersion)
                    && scenario.updateSchedule() != null
                    && scenario.updateSchedule().type()
                            != UpdateScheduleType.SYNCHRONOUS) {
                throw new IllegalArgumentException(
                        "schemaVersion 0.1 supports only synchronous updates");
            }
        }
    }

    /**
     * Returns the total number of independent runs.
     */
    public int runCount() {
        int count = 0;
        for (ScenarioSpec scenario : scenarios) {
            count = Math.addExact(count, scenario.runCount());
        }
        return count;
    }
}
