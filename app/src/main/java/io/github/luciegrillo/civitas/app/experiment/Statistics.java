package io.github.luciegrillo.civitas.app.experiment;

import java.util.ArrayList;
import java.util.List;

/**
 * Small deterministic descriptive-statistics helpers.
 */
public final class Statistics {
    private Statistics() {
    }

    /**
     * Arithmetic mean.
     */
    public static double mean(List<Double> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    /**
     * Linear-interpolation quantile using index {@code q * (n - 1)}.
     */
    public static double quantile(List<Double> values, double q) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        if (!Double.isFinite(q) || q < 0.0 || q > 1.0) {
            throw new IllegalArgumentException("q must be in [0, 1]");
        }
        ArrayList<Double> sorted = new ArrayList<>(values);
        sorted.sort(Double::compare);
        double position = q * (sorted.size() - 1);
        int lower = (int) Math.floor(position);
        int upper = (int) Math.ceil(position);
        if (lower == upper) {
            return sorted.get(lower);
        }
        double weight = position - lower;
        return sorted.get(lower) * (1.0 - weight) + sorted.get(upper) * weight;
    }
}
