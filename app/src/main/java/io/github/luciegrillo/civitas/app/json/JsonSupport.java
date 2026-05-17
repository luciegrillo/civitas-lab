package io.github.luciegrillo.civitas.app.json;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Shared strict JSON configuration.
 */
public final class JsonSupport {
    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .enable(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                    DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY,
                    DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    private JsonSupport() {
    }

    /**
     * Returns the thread-safe application mapper.
     */
    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
