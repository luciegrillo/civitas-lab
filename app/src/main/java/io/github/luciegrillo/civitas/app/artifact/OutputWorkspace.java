package io.github.luciegrillo.civitas.app.artifact;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.Objects;

/**
 * Owns and safeguards one staged experiment output directory.
 */
public final class OutputWorkspace implements AutoCloseable {
    private final Path requestedRoot;
    private final Path stagingRoot;
    private final boolean overwrite;
    private boolean published;

    private OutputWorkspace(Path requestedRoot, Path stagingRoot, boolean overwrite) {
        this.requestedRoot = requestedRoot;
        this.stagingRoot = stagingRoot;
        this.overwrite = overwrite;
    }

    /**
     * Creates an isolated staging directory beside the requested output.
     */
    public static OutputWorkspace prepare(Path requested, boolean overwrite) throws IOException {
        Objects.requireNonNull(requested, "requested");
        Path requestedRoot = requested.toAbsolutePath().normalize();
        Path workingDirectory = Path.of("").toAbsolutePath().normalize();
        if (requestedRoot.getParent() == null || requestedRoot.equals(workingDirectory)) {
            throw new IOException("refusing to use filesystem root or working directory as output");
        }
        rejectSymbolicLinkComponents(requestedRoot);
        if (Files.exists(requestedRoot, LinkOption.NOFOLLOW_LINKS) && !overwrite) {
            throw new IOException(
                    "output directory already exists; use --overwrite to replace it: "
                            + requestedRoot);
        }

        Path parent = requestedRoot.getParent();
        Files.createDirectories(parent);
        rejectSymbolicLinkComponents(parent);
        String name = requestedRoot.getFileName().toString();
        Path stagingRoot = Files.createTempDirectory(parent, "." + name + ".staging-");
        Files.createDirectories(stagingRoot.resolve("runs"));
        Files.createDirectories(stagingRoot.resolve("figures"));
        return new OutputWorkspace(requestedRoot, stagingRoot, overwrite);
    }

    /**
     * Returns the staging directory used while the experiment is running.
     */
    public Path root() {
        return stagingRoot;
    }

    /**
     * Returns the final output path requested by the caller.
     */
    public Path requestedRoot() {
        return requestedRoot;
    }

    public Path runDirectory(String runId) throws IOException {
        Path directory = stagingRoot.resolve("runs").resolve(runId);
        Files.createDirectories(directory.resolve("snapshots"));
        return directory;
    }

    /**
     * Publishes the completed staging directory at the requested output path.
     */
    public Path publish() throws IOException {
        if (published) {
            throw new IllegalStateException("workspace has already been published");
        }

        Path backup = null;
        boolean destinationMoved = false;
        try {
            if (Files.exists(requestedRoot, LinkOption.NOFOLLOW_LINKS)) {
                if (!overwrite) {
                    throw new IOException("output directory appeared during experiment: "
                            + requestedRoot);
                }
                rejectSymbolicLinkComponents(requestedRoot);
                backup = uniqueBackupPath();
                move(requestedRoot, backup);
                destinationMoved = true;
            }

            move(stagingRoot, requestedRoot);
            published = true;
            deleteBackupBestEffort(backup);
            return requestedRoot;
        } catch (IOException exception) {
            if (destinationMoved && backup != null
                    && !Files.exists(requestedRoot, LinkOption.NOFOLLOW_LINKS)) {
                try {
                    move(backup, requestedRoot);
                } catch (IOException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
            }
            throw exception;
        }
    }

    @Override
    public void close() throws IOException {
        if (!published && Files.exists(stagingRoot, LinkOption.NOFOLLOW_LINKS)) {
            deleteTree(stagingRoot);
        }
    }

    private Path uniqueBackupPath() throws IOException {
        Path parent = requestedRoot.getParent();
        String name = requestedRoot.getFileName().toString();
        Path marker = Files.createTempFile(parent, "." + name + ".backup-", "");
        Files.delete(marker);
        return marker;
    }

    private static void deleteBackupBestEffort(Path backup) {
        if (backup == null) {
            return;
        }
        try {
            deleteTree(backup);
        } catch (IOException ignored) {
            // Publication has already succeeded. A stale backup is safer than
            // reporting failure after exposing a complete requested output.
        }
    }

    private static void move(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target);
        }
    }

    private static void deleteTree(Path root) throws IOException {
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
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
