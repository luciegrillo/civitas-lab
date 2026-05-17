package io.github.luciegrillo.civitas.app.experiment;

import java.nio.file.Path;

/**
 * User-facing completion summary.
 */
public record ExecutionReport(Path outputDirectory, int runCount, long elapsedMillis) {
}
