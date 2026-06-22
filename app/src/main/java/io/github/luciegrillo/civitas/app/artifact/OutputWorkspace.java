package io.github.luciegrillo.civitas.app.artifact;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;

/**
 * Owns and safeguards one experiment output directory.
 */
public final class OutputWorkspace {
    private final Path root;

    private OutputWorkspace(Path root) {
        this.root = root;
    }

    /**
     * Creates an isolated output directory.
     */
    public static OutputWorkspace prepare(Path requested, boolean overwrite) throws IOException {
        Objects.requireNonNull(requested, "requested");
        Path root = requested.toAbsolutePath().normalize();
        Path workingDirectory = Path.of("").toAbsolutePath().normalize();
        if (root.getParent() == null || root.equals(workingDirectory)) {
            throw new IOException("refusing to use filesystem root or working directory as output");
        }
        rejectSymbolicLinkComponents(root);
        if (Files.exists(root, LinkOption.NOFOLLOW_LINKS)) {
            if (!overwrite) {
                throw new IOException(
                        "output directory already exists; use --overwrite to replace it: " + root);
            }
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
        Files.createDirectories(root.resolve("runs"));
        Files.createDirectories(root.resolve("figures"));
        return new OutputWorkspace(root);
    }

    public Path root() {
        return root;
    }

    public Path runDirectory(String runId) throws IOException {
        Path directory = root.resolve("runs").resolve(runId);
        Files.createDirectories(directory.resolve("snapshots"));
        return directory;
    }

    private static void rejectSymbolicLinkComponents(Path root) throws IOException {
        Path current = root.getRoot();
        for (Path name : root) {
            current = current == null ? name : current.resolve(name);
            if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)
                    && Files.isSymbolicLink(current)) {
                throw new IOException("refusing symlinked output path: " + current);
            }
        }
    }
}
