package io.github.luciegrillo.civitas.app.artifact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OutputWorkspaceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void stagesBesideRequestedOutputAndPublishesOnlyWhenComplete() throws IOException {
        Path output = temporaryDirectory.resolve("artifacts");
        Path staging;

        try (OutputWorkspace workspace = OutputWorkspace.prepare(output, false)) {
            staging = workspace.root();
            assertEquals(temporaryDirectory, staging.getParent());
            assertTrue(staging.getFileName().toString().startsWith(".artifacts.staging-"));
            assertFalse(Files.exists(output));

            Files.writeString(staging.resolve("complete.txt"), "complete\n");
            assertEquals(output.toAbsolutePath(), workspace.publish());
        }

        assertFalse(Files.exists(staging));
        assertEquals("complete\n", Files.readString(output.resolve("complete.txt")));
    }

    @Test
    void removesUnpublishedStagingDirectoryOnClose() throws IOException {
        Path output = temporaryDirectory.resolve("artifacts");
        Path staging;

        try (OutputWorkspace workspace = OutputWorkspace.prepare(output, false)) {
            staging = workspace.root();
            Files.writeString(staging.resolve("partial.txt"), "partial\n");
        }

        assertFalse(Files.exists(staging));
        assertFalse(Files.exists(output));
    }

    @Test
    void overwritePreservesExistingOutputUntilPublication() throws IOException {
        Path output = temporaryDirectory.resolve("artifacts");
        Files.createDirectories(output);
        Files.writeString(output.resolve("old.txt"), "old\n");

        try (OutputWorkspace workspace = OutputWorkspace.prepare(output, true)) {
            Files.writeString(workspace.root().resolve("new.txt"), "new\n");
            assertEquals("old\n", Files.readString(output.resolve("old.txt")));
            workspace.publish();
        }

        assertFalse(Files.exists(output.resolve("old.txt")));
        assertEquals("new\n", Files.readString(output.resolve("new.txt")));
    }

    @Test
    void rejectsExistingOutputWithoutOverwrite() throws IOException {
        Path output = temporaryDirectory.resolve("artifacts");
        Files.createDirectories(output);

        IOException exception = assertThrows(
                IOException.class,
                () -> OutputWorkspace.prepare(output, false));

        assertTrue(exception.getMessage().contains("use --overwrite"));
    }

    @Test
    void refusesOverwriteWhenOutputRootIsSymlink() throws IOException {
        Path target = temporaryDirectory.resolve("target-output");
        Path linkedOutput = temporaryDirectory.resolve("linked-output");
        Files.createDirectories(target);
        Files.writeString(target.resolve("kept.txt"), "keep\n");
        createSymbolicLink(linkedOutput, target);

        IOException exception = assertThrows(
                IOException.class,
                () -> OutputWorkspace.prepare(linkedOutput, true));

        assertTrue(exception.getMessage().contains("refusing symlinked output path"));
        assertTrue(Files.isRegularFile(target.resolve("kept.txt")));
    }

    @Test
    void refusesOutputUnderSymlinkedParent() throws IOException {
        Path targetParent = temporaryDirectory.resolve("target-parent");
        Path linkedParent = temporaryDirectory.resolve("linked-parent");
        Files.createDirectories(targetParent);
        createSymbolicLink(linkedParent, targetParent);

        IOException exception = assertThrows(
                IOException.class,
                () -> OutputWorkspace.prepare(linkedParent.resolve("output"), false));

        assertTrue(exception.getMessage().contains("refusing symlinked output path"));
        assertFalse(Files.exists(targetParent.resolve("output"), LinkOption.NOFOLLOW_LINKS));
    }

    private static void createSymbolicLink(Path link, Path target) throws IOException {
        try {
            Files.createSymbolicLink(link, target);
        } catch (UnsupportedOperationException | FileSystemException exception) {
            assumeTrue(false, "symbolic links are not available: " + exception.getMessage());
        }
    }
}
