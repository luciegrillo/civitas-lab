package io.github.luciegrillo.civitas.app;

/**
 * Application version resolved from the packaged manifest.
 */
public final class VersionInfo {
    private static final String DEVELOPMENT_VERSION = "0.1.0-SNAPSHOT";

    private VersionInfo() {
    }

    public static String version() {
        String value = VersionInfo.class.getPackage().getImplementationVersion();
        return value == null || value.isBlank() ? DEVELOPMENT_VERSION : value;
    }
}
