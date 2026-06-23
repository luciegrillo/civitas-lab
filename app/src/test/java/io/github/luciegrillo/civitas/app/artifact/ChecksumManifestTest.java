package io.github.luciegrillo.civitas.app.artifact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ChecksumManifestTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void detectsChangedArtifacts() throws IOException {
        Files.writeString(temporaryDirectory.resolve("result.csv"), "value\n1\n");
        Files.writeString(temporaryDirectory.resolve("provenance.json"), "volatile");
        ChecksumManifest.write(temporaryDirectory);

        assertEquals(0, ChecksumManifest.validate(temporaryDirectory).size());
        String manifest = Files.readString(
                temporaryDirectory.resolve(ChecksumManifest.FILE_NAME));
        assertTrue(manifest.contains("result.csv"));
        org.junit.jupiter.api.Assertions.assertFalse(manifest.contains("provenance.json"));

        Files.writeString(temporaryDirectory.resolve("result.csv"), "value\n2\n");
        assertTrue(ChecksumManifest.validate(temporaryDirectory).getFirst()
                .contains("checksum mismatch"));
    }

    @Test
    void refusesToWriteSymlinkedArtifacts() throws IOException {
        Files.writeString(temporaryDirectory.resolve("result.csv"), "value\n1\n");
        createSymbolicLink(temporaryDirectory.resolve("link.csv"), Path.of("result.csv"));

        IOException exception = assertThrows(
                IOException.class,
                () -> ChecksumManifest.write(temporaryDirectory));

        assertTrue(exception.getMessage().contains("symlinked artifact path: link.csv"));
        assertFalse(Files.exists(temporaryDirectory.resolve(ChecksumManifest.FILE_NAME)));
    }

    @Test
    void reportsSymlinkedManifestFile() throws IOException {
        Files.writeString(temporaryDirectory.resolve("manifest-target.txt"), "not trusted\n");
        createSymbolicLink(
                temporaryDirectory.resolve(ChecksumManifest.FILE_NAME),
                Path.of("manifest-target.txt"));

        assertEquals(
                List.of("symlinked artifact path: " + ChecksumManifest.FILE_NAME),
                ChecksumManifest.validate(temporaryDirectory));
    }

    @Test
    void reportsSymlinkedManifestEntries() throws IOException {
        Files.writeString(temporaryDirectory.resolve("result.csv"), "value\n1\n");
        createSymbolicLink(temporaryDirectory.resolve("link.csv"), Path.of("result.csv"));
        Files.writeString(
                temporaryDirectory.resolve(ChecksumManifest.FILE_NAME),
                "0".repeat(64) + "  link.csv\n");

        assertEquals(
                List.of("symlinked artifact path: link.csv"),
                ChecksumManifest.validate(temporaryDirectory));
    }

    @Test
    void reportsEscapingManifestPaths() throws IOException {
        Files.writeString(
                temporaryDirectory.resolve(ChecksumManifest.FILE_NAME),
                "0".repeat(64) + "  nested/../../outside.txt\n");

        assertEquals(
                List.of("path escapes output directory: nested/../../outside.txt"),
                ChecksumManifest.validate(temporaryDirectory));
    }

    @Test
    void reportsMalformedManifestLines() throws IOException {
        Files.writeString(
                temporaryDirectory.resolve(ChecksumManifest.FILE_NAME),
                "not a sha256 manifest line\n");

        assertEquals(
                List.of("invalid manifest line 1"),
                ChecksumManifest.validate(temporaryDirectory));
    }

    private static void createSymbolicLink(Path link, Path target) throws IOException {
        try {
            Files.createSymbolicLink(link, target);
        } catch (UnsupportedOperationException | FileSystemException exception) {
            assumeTrue(false, "symbolic links are not available: " + exception.getMessage());
        }
    }
}
