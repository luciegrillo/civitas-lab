package io.github.luciegrillo.civitas.app.config;

/**
 * Supported public update schedules for spatial experiments.
 */
public enum UpdateScheduleType {
    /** Double-buffered generation updates. */
    SYNCHRONOUS,

    /** Shuffled in-place sweeps without replacement. */
    RANDOM_SEQUENTIAL
}
