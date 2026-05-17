package io.github.luciegrillo.civitas.app.artifact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
