package io.github.luciegrillo.civitas.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Serialized initial-condition definition.
 */
public record InitializationSpec(
        @JsonProperty(required = true) InitializationType type,
        @JsonProperty(required = false) Double pCooperator) {

    public InitializationSpec {
        Objects.requireNonNull(type, "initialization type");
        if (type == InitializationType.BERNOULLI) {
            if (pCooperator == null
                    || !Double.isFinite(pCooperator)
                    || pCooperator < 0.0
                    || pCooperator > 1.0) {
                throw new IllegalArgumentException(
                        "BERNOULLI initialization requires pCooperator in [0, 1]");
            }
        } else if (pCooperator != null) {
            throw new IllegalArgumentException(
                    "CENTRAL_DEFECTOR initialization must not define pCooperator");
        }
    }
}
