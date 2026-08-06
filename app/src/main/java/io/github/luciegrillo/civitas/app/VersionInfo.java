package io.github.luciegrillo.civitas.app;

/**
 * Application version resolved from the packaged manifest.
 */
public final class VersionInfo {
    private static final String FALLBACK_VERSION = "0.2.0";

    private VersionInfo() {
    }

    public static String version() {
        String value = VersionInfo.class.getPackage().getImplementationVersion();
        return value == null || value.isBlank() ? FALLBACK_VERSION : value;
    }
}
