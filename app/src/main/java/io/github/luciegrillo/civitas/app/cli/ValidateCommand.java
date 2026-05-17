package io.github.luciegrillo.civitas.app.cli;

import io.github.luciegrillo.civitas.app.artifact.ChecksumManifest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

/**
 * Verifies a generated artifact directory.
 */
@Command(name = "validate", description = "Validate artifact SHA-256 checksums.")
public final class ValidateCommand implements Callable<Integer> {
    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", paramLabel = "DIRECTORY", description = "Artifact directory.")
    private Path directory;

    @Override
    public Integer call() {
        Path normalized = directory.toAbsolutePath().normalize();
        if (!Files.isDirectory(normalized)) {
            spec.commandLine().getErr()
                    .println("error: artifact directory does not exist: " + normalized);
            return 1;
        }
        try {
            List<String> problems = ChecksumManifest.validate(normalized);
            if (!problems.isEmpty()) {
                problems.forEach(
                        problem -> spec.commandLine().getErr().println("error: " + problem));
                return 1;
            }
            spec.commandLine().getOut()
                    .println("Artifact checksums are valid: " + normalized);
            return 0;
        } catch (IOException exception) {
            spec.commandLine().getErr().println("error: " + exception.getMessage());
            return 1;
        }
    }
}
