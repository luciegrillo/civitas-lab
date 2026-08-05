package io.github.luciegrillo.civitas.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.luciegrillo.civitas.core.WeakPrisonersDilemma;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * One family of runs within an experiment.
 */
public record ScenarioSpec(
        @JsonProperty(required = true) String id,
        @JsonProperty(required = true) String seedGroup,
        @JsonProperty(required = true) LatticeSpec lattice,
        @JsonProperty(required = true) InitializationSpec initialization,
        @JsonProperty(required = true) boolean selfInteraction,
        @JsonProperty(required = true) List<Double> temptationValues,
        @JsonProperty(required = true) int replicates,
        @JsonProperty(required = true) int ticks,
        @JsonProperty(required = true) int measurementStart,
        @JsonProperty(required = true) List<Integer> snapshotTicks,
        @JsonInclude(JsonInclude.Include.NON_NULL) UpdateScheduleSpec updateSchedule) {

    /**
     * Creates a synchronous scenario for programmatic callers using the legacy
     * constructor shape.
     */
    public ScenarioSpec(
            String id,
            String seedGroup,
            LatticeSpec lattice,
            InitializationSpec initialization,
            boolean selfInteraction,
            List<Double> temptationValues,
            int replicates,
            int ticks,
            int measurementStart,
            List<Integer> snapshotTicks) {
        this(
                id,
                seedGroup,
                lattice,
                initialization,
                selfInteraction,
                temptationValues,
                replicates,
                ticks,
                measurementStart,
                snapshotTicks,
                UpdateScheduleSpec.synchronous());
    }

    public ScenarioSpec {
        id = requireIdentifier(id, "scenario id");
        seedGroup = requireIdentifier(seedGroup, "seedGroup");
        Objects.requireNonNull(lattice, "lattice");
        Objects.requireNonNull(initialization, "initialization");
        temptationValues = List.copyOf(Objects.requireNonNull(
                temptationValues, "temptationValues"));
        snapshotTicks = List.copyOf(Objects.requireNonNull(
                snapshotTicks, "snapshotTicks"));

        if (temptationValues.isEmpty()) {
            throw new IllegalArgumentException("temptationValues must not be empty");
        }
        for (double temptation : temptationValues) {
            new WeakPrisonersDilemma(temptation);
        }
        if (new HashSet<>(temptationValues).size() != temptationValues.size()) {
            throw new IllegalArgumentException("temptationValues must be unique");
        }
        if (replicates < 1) {
            throw new IllegalArgumentException("replicates must be positive");
        }
        if (ticks < 1) {
            throw new IllegalArgumentException("ticks must be positive");
        }
        if (measurementStart < 0 || measurementStart > ticks) {
            throw new IllegalArgumentException("measurementStart must be in [0, ticks]");
        }
        if (new HashSet<>(snapshotTicks).size() != snapshotTicks.size()) {
            throw new IllegalArgumentException("snapshotTicks must be unique");
        }
        for (int tick : snapshotTicks) {
            if (tick < 0 || tick > ticks) {
                throw new IllegalArgumentException("snapshotTicks must be in [0, ticks]");
            }
        }
        snapshotTicks = snapshotTicks.stream().sorted().toList();

        if (initialization.type() == InitializationType.CENTRAL_DEFECTOR
                && ((lattice.width() & 1) == 0 || (lattice.height() & 1) == 0)) {
            throw new IllegalArgumentException(
                    "CENTRAL_DEFECTOR requires odd lattice dimensions");
        }
    }

    /**
     * Returns the schedule used by the simulation. Schema 0.1 scenarios omit
     * the field and therefore retain synchronous semantics.
     */
    public UpdateScheduleSpec effectiveUpdateSchedule() {
        return updateSchedule == null ? UpdateScheduleSpec.synchronous() : updateSchedule;
    }

    /**
     * Returns the number of independent runs expanded by this scenario.
     */
    public int runCount() {
        return Math.multiplyExact(temptationValues.size(), replicates);
    }

    private static String requireIdentifier(String value, String label) {
        Objects.requireNonNull(value, label);
        String normalized = value.strip();
        if (!normalized.matches("[a-z0-9][a-z0-9-]*")) {
            throw new IllegalArgumentException(
                    label + " must match [a-z0-9][a-z0-9-]*");
        }
        return normalized;
    }
}
