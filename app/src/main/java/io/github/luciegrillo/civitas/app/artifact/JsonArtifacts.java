package io.github.luciegrillo.civitas.app.artifact;

import io.github.luciegrillo.civitas.app.json.JsonSupport;
import java.io.IOException;
import java.nio.file.Path;
import tools.jackson.core.JacksonException;

/**
 * Stable pretty-printed JSON output.
 */
public final class JsonArtifacts {
    private JsonArtifacts() {
    }

    public static void write(Path path, Object value) throws IOException {
        try {
            JsonSupport.mapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(path, value);
        } catch (JacksonException exception) {
            throw new IOException("could not write JSON artifact: " + path, exception);
        }
    }
}
