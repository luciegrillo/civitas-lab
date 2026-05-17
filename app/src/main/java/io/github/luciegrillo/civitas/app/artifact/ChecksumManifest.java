package io.github.luciegrillo.civitas.app.artifact;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

/**
 * Writes and validates SHA-256 manifests for deterministic artifacts.
 */
public final class ChecksumManifest {
    public static final String FILE_NAME = "checksums.sha256";

    private ChecksumManifest() {
    }

    /**
     * Hashes all regular files except provenance and the manifest itself.
     */
    public static void write(Path root) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        try (var paths = Files.walk(root)) {
            List<Path> files = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.equals(root.resolve(FILE_NAME)))
                    .filter(path -> !path.equals(root.resolve("provenance.json")))
                    .sorted()
                    .toList();
            for (Path file : files) {
                String relative = root.relativize(file).toString().replace('\\', '/');
                lines.add(hash(file) + "  " + relative);
            }
        }
        Files.write(
                root.resolve(FILE_NAME),
                lines,
                StandardCharsets.UTF_8);
    }

    /**
     * Verifies every entry and returns human-readable problems.
     */
    public static List<String> validate(Path root) throws IOException {
        Path manifest = root.resolve(FILE_NAME);
        if (!Files.isRegularFile(manifest)) {
            return List.of("missing " + FILE_NAME);
        }

        ArrayList<String> problems = new ArrayList<>();
        int lineNumber = 0;
        for (String line : Files.readAllLines(manifest, StandardCharsets.UTF_8)) {
            lineNumber++;
            if (line.isBlank()) {
                continue;
            }
            if (!line.matches("[0-9a-f]{64}  .+")) {
                problems.add("invalid manifest line " + lineNumber);
                continue;
            }
            String expected = line.substring(0, 64);
            String relative = line.substring(66);
            Path file = root.resolve(relative).normalize();
            if (!file.startsWith(root.normalize())) {
                problems.add("path escapes output directory: " + relative);
            } else if (!Files.isRegularFile(file)) {
                problems.add("missing file: " + relative);
            } else {
                String actual = hash(file);
                if (!expected.equals(actual)) {
                    problems.add("checksum mismatch: " + relative);
                }
            }
        }
        return List.copyOf(problems);
    }

    private static String hash(Path file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
        try (InputStream input = Files.newInputStream(file)) {
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = input.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
