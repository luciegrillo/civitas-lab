package io.github.luciegrillo.civitas.app.artifact;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
        Path normalizedRoot = root.toAbsolutePath().normalize();
        rejectIfSymbolicLink(normalizedRoot, normalizedRoot);
        ArrayList<String> lines = new ArrayList<>();
        try (var paths = Files.walk(normalizedRoot)) {
            List<Path> files = new ArrayList<>();
            for (Path path : paths.sorted().toList()) {
                rejectIfSymbolicLink(path, normalizedRoot);
                if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
                    continue;
                }
                if (path.equals(normalizedRoot.resolve(FILE_NAME))
                        || path.equals(normalizedRoot.resolve("provenance.json"))) {
                    continue;
                }
                files.add(path);
            }
            for (Path file : files) {
                String relative = normalizedRoot.relativize(file).toString().replace('\\', '/');
                lines.add(hash(file) + "  " + relative);
            }
        }
        Files.write(
                normalizedRoot.resolve(FILE_NAME),
                lines,
                StandardCharsets.UTF_8);
    }

    /**
     * Verifies every entry and returns human-readable problems.
     */
    public static List<String> validate(Path root) throws IOException {
        Path normalizedRoot = root.toAbsolutePath().normalize();
        if (containsSymbolicLink(normalizedRoot)) {
            return List.of("symlinked artifact path: .");
        }
        Path manifest = normalizedRoot.resolve(FILE_NAME);
        if (containsSymbolicLink(manifest)) {
            return List.of("symlinked artifact path: " + FILE_NAME);
        }
        if (!Files.isRegularFile(manifest, LinkOption.NOFOLLOW_LINKS)) {
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
            Path file = normalizedRoot.resolve(relative).normalize();
            if (!file.startsWith(normalizedRoot)) {
                problems.add("path escapes output directory: " + relative);
            } else if (containsSymbolicLink(file)) {
                problems.add("symlinked artifact path: " + relative);
            } else if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
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
        if (containsSymbolicLink(file)) {
            throw new IOException("refusing to hash symlinked artifact path: " + file);
        }
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
        try (InputStream input = Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS)) {
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = input.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static void rejectIfSymbolicLink(Path path, Path root) throws IOException {
        if (containsSymbolicLink(path)) {
            String relative = path.equals(root)
                    ? "."
                    : root.relativize(path).toString().replace('\\', '/');
            throw new IOException("symlinked artifact path: " + relative);
        }
    }

    private static boolean containsSymbolicLink(Path path) throws IOException {
        Path absolute = path.toAbsolutePath().normalize();
        Path current = absolute.getRoot();
        for (Path name : absolute) {
            current = current == null ? name : current.resolve(name);
            if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)
                    && Files.isSymbolicLink(current)) {
                return true;
            }
        }
        return false;
    }
}
