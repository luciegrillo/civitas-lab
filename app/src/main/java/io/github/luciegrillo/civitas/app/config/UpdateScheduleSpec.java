package io.github.luciegrillo.civitas.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Versioned update-schedule parameters for one scenario.
 *
 * @param type schedule mechanism
 */
public record UpdateScheduleSpec(
        @JsonProperty(required = true) UpdateScheduleType type) {

    public UpdateScheduleSpec {
        Objects.requireNonNull(type, "type");
    }

    /**
     * Returns the synchronous baseline schedule.
     */
    public static UpdateScheduleSpec synchronous() {
        return new UpdateScheduleSpec(UpdateScheduleType.SYNCHRONOUS);
    }
}
