package io.github.luciegrillo.civitas.core;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable rectangular lattice of binary strategies.
 */
public final class Lattice {
    private final int width;
    private final int height;
    private final byte[] cells;

    private Lattice(int width, int height, byte[] cells, boolean trusted) {
        if (width < 1 || height < 1 || Math.multiplyExact(width, height) != cells.length) {
            throw new IllegalArgumentException("cell count does not match lattice dimensions");
        }
        validateCodes(cells);
        this.width = width;
        this.height = height;
        this.cells = trusted ? cells : cells.clone();
    }

    /**
     * Creates a lattice from strategy codes ({@code 0=C}, {@code 1=D}).
     *
     * @param width lattice width
     * @param height lattice height
     * @param cells row-major strategy codes
     * @return an immutable lattice
     */
    public static Lattice fromCodes(int width, int height, byte[] cells) {
        Objects.requireNonNull(cells, "cells");
        return new Lattice(width, height, cells, false);
    }

    /**
     * Creates a lattice filled with one strategy.
     *
     * @param width lattice width
     * @param height lattice height
     * @param strategy strategy assigned to every site
     * @return an immutable lattice
     */
    public static Lattice filled(int width, int height, Strategy strategy) {
        Objects.requireNonNull(strategy, "strategy");
        requirePositiveDimensions(width, height);
        byte[] cells = new byte[Math.multiplyExact(width, height)];
        Arrays.fill(cells, strategy.code());
        return new Lattice(width, height, cells, true);
    }

    static Lattice copyOfInternal(int width, int height, byte[] cells) {
        return new Lattice(width, height, cells.clone(), true);
    }

    /**
     * Returns the lattice width.
     *
     * @return width in sites
     */
    public int width() {
        return width;
    }

    /**
     * Returns the lattice height.
     *
     * @return height in sites
     */
    public int height() {
        return height;
    }

    /**
     * Returns the strategy at a coordinate.
     *
     * @param x zero-based horizontal coordinate
     * @param y zero-based vertical coordinate
     * @return strategy at the coordinate
     */
    public Strategy strategyAt(int x, int y) {
        Objects.checkIndex(x, width);
        Objects.checkIndex(y, height);
        return Strategy.fromCode(cells[y * width + x]);
    }

    /**
     * Returns a defensive copy of the row-major strategy codes.
     *
     * @return copied codes ({@code 0=C}, {@code 1=D})
     */
    public byte[] copyCodes() {
        return cells.clone();
    }

    /**
     * Counts sites using the requested strategy.
     *
     * @param strategy strategy to count
     * @return number of matching sites
     */
    public int count(Strategy strategy) {
        Objects.requireNonNull(strategy, "strategy");
        int count = 0;
        for (byte cell : cells) {
            if (cell == strategy.code()) {
                count++;
            }
        }
        return count;
    }

    private static void validateCodes(byte[] cells) {
        for (byte cell : cells) {
            Strategy.fromCode(cell);
        }
    }

    private static void requirePositiveDimensions(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException("width and height must be positive");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Lattice lattice)) {
            return false;
        }
        return width == lattice.width
                && height == lattice.height
                && Arrays.equals(cells, lattice.cells);
    }

    @Override
    public int hashCode() {
        int result = 31 * width + height;
        return 31 * result + Arrays.hashCode(cells);
    }

    @Override
    public String toString() {
        return "Lattice[width=" + width + ", height=" + height + "]";
    }
}
