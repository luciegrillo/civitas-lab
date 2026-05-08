package io.github.luciegrillo.civitas.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MooreNeighborhoodTest {

    @Test
    void toroidalNeighborhoodHasEightUniqueSitesAtCorner() {
        MooreNeighborhood neighborhood =
                MooreNeighborhood.create(5, 5, BoundaryCondition.TOROIDAL);
        int corner = neighborhood.index(0, 0);
        Set<Integer> neighbors = collect(neighborhood, corner);

        assertEquals(8, neighborhood.count(corner));
        assertEquals(8, neighbors.size());
        assertFalse(neighbors.contains(corner));
        org.junit.jupiter.api.Assertions.assertTrue(
                neighbors.contains(neighborhood.index(4, 4)));
    }

    @Test
    void boundedNeighborhoodOmitsOutOfBoundsSites() {
        MooreNeighborhood neighborhood =
                MooreNeighborhood.create(5, 5, BoundaryCondition.BOUNDED);

        assertEquals(3, neighborhood.count(neighborhood.index(0, 0)));
        assertEquals(5, neighborhood.count(neighborhood.index(2, 0)));
        assertEquals(8, neighborhood.count(neighborhood.index(2, 2)));
    }

    @Test
    void validatesNeighborOffset() {
        MooreNeighborhood neighborhood =
                MooreNeighborhood.create(5, 5, BoundaryCondition.BOUNDED);
        int corner = neighborhood.index(0, 0);

        assertThrows(
                IndexOutOfBoundsException.class,
                () -> neighborhood.neighbor(corner, 3));
    }

    private static Set<Integer> collect(MooreNeighborhood neighborhood, int site) {
        Set<Integer> result = new HashSet<>();
        for (int offset = 0; offset < neighborhood.count(site); offset++) {
            result.add(neighborhood.neighbor(site, offset));
        }
        return result;
    }
}
