package io.github.luciegrillo.civitas.core;

import java.util.Objects;

/**
 * Precomputed range-one Moore neighborhoods for a rectangular lattice.
 *
 * <p>The focal site is not part of the returned neighborhood. A toroidal site
 * has eight neighbors; bounded edge sites have fewer.</p>
 */
public final class MooreNeighborhood {
    private static final int MAX_NEIGHBORS = 8;

    private final int width;
    private final int height;
    private final int[] neighbors;
    private final byte[] counts;

    private MooreNeighborhood(
            int width, int height, int[] neighbors, byte[] counts) {
        this.width = width;
        this.height = height;
        this.neighbors = neighbors;
        this.counts = counts;
    }

    /**
     * Precomputes neighborhoods for the supplied lattice.
     *
     * @param width lattice width, at least three
     * @param height lattice height, at least three
     * @param boundaryCondition edge behavior
     * @return immutable neighborhood lookup
     */
    public static MooreNeighborhood create(
            int width, int height, BoundaryCondition boundaryCondition) {
        if (width < 3 || height < 3) {
            throw new IllegalArgumentException("width and height must be at least 3");
        }
        Objects.requireNonNull(boundaryCondition, "boundaryCondition");

        int siteCount = Math.multiplyExact(width, height);
        int[] neighbors = new int[Math.multiplyExact(siteCount, MAX_NEIGHBORS)];
        byte[] counts = new byte[siteCount];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int site = y * width + x;
                int count = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dx == 0 && dy == 0) {
                            continue;
                        }

                        int neighborX = x + dx;
                        int neighborY = y + dy;
                        if (boundaryCondition == BoundaryCondition.TOROIDAL) {
                            neighborX = wrap(neighborX, width);
                            neighborY = wrap(neighborY, height);
                        } else if (neighborX < 0 || neighborX >= width
                                || neighborY < 0 || neighborY >= height) {
                            continue;
                        }

                        neighbors[site * MAX_NEIGHBORS + count] =
                                neighborY * width + neighborX;
                        count++;
                    }
                }
                counts[site] = (byte) count;
            }
        }
        return new MooreNeighborhood(width, height, neighbors, counts);
    }

    /**
     * Returns the number of neighbors for one row-major site.
     *
     * @param siteIndex row-major site index
     * @return neighbor count
     */
    public int count(int siteIndex) {
        Objects.checkIndex(siteIndex, counts.length);
        return Byte.toUnsignedInt(counts[siteIndex]);
    }

    /**
     * Returns a neighboring row-major site index.
     *
     * @param siteIndex focal row-major site index
     * @param neighborOffset offset from zero to {@link #count(int)} exclusive
     * @return neighboring site index
     */
    public int neighbor(int siteIndex, int neighborOffset) {
        Objects.checkIndex(siteIndex, counts.length);
        Objects.checkIndex(neighborOffset, count(siteIndex));
        return neighbors[siteIndex * MAX_NEIGHBORS + neighborOffset];
    }

    /**
     * Returns the row-major index for a coordinate.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return row-major index
     */
    public int index(int x, int y) {
        Objects.checkIndex(x, width);
        Objects.checkIndex(y, height);
        return y * width + x;
    }

    private static int wrap(int coordinate, int size) {
        if (coordinate < 0) {
            return size - 1;
        }
        if (coordinate == size) {
            return 0;
        }
        return coordinate;
    }
}
