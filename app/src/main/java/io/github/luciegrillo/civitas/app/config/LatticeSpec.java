package io.github.luciegrillo.civitas.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.luciegrillo.civitas.core.BoundaryCondition;
import java.util.Objects;

/**
 * Serialized lattice definition.
 */
public record LatticeSpec(
        @JsonProperty(required = true) int width,
        @JsonProperty(required = true) int height,
        @JsonProperty(required = true) BoundaryCondition boundary) {

    public LatticeSpec {
        if (width < 3 || height < 3) {
            throw new IllegalArgumentException("lattice width and height must be at least 3");
        }
        Objects.requireNonNull(boundary, "lattice boundary");
        Math.multiplyExact(width, height);
    }
}
