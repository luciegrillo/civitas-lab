package io.github.luciegrillo.civitas.app.artifact;

import io.github.luciegrillo.civitas.app.VersionInfo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * Runtime metadata that is informative but excluded from deterministic hashes.
 */
public record Provenance(
        String generatedAt,
        String civitasVersion,
        String gitRevision,
        String javaVersion,
        String javaVendor,
        String operatingSystem,
        String architecture) {

    public static Provenance capture() {
        return new Provenance(
                Instant.now().toString(),
                VersionInfo.version(),
                detectGitRevision(),
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                System.getProperty("os.name") + " " + System.getProperty("os.version"),
                System.getProperty("os.arch"));
    }

    private static String detectGitRevision() {
        Process process = null;
        try {
            process = new ProcessBuilder("git", "rev-parse", "HEAD")
                    .redirectErrorStream(true)
                    .start();
            boolean completed = process.waitFor(2, TimeUnit.SECONDS);
            if (!completed || process.exitValue() != 0) {
                return "unknown";
            }
            return new String(
                    process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
        } catch (IOException exception) {
            return "unknown";
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return "unknown";
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }
}
