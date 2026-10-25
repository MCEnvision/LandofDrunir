package org.mcenvision.drunir.boulder;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoulderJoinTest {
    private static final int FULL = BoulderJoin.piece(8, 0, 0);

    @Test
    void removingNeighborsRestoresTheExactStandaloneShape() {
        for (int form = 0; form < 4; form++) {
            for (int height = 1; height <= 8; height++) {
                var isolated = new BoulderJoin(BoulderJoin.piece(height, form, 1), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                assertEquals(BoulderCatalog.INSTANCE.geometry(form, height, 1), isolated.geometry());
            }
        }
    }

    @Test
    void differentFormsAndHeightsHaveTheSameSolidSharedFace() {
        for (int leftHeight = 1; leftHeight <= 8; leftHeight++) {
            for (int rightHeight = 1; rightHeight <= 8; rightHeight++) {
                int left = BoulderJoin.piece(leftHeight, 1, 2);
                int right = BoulderJoin.piece(rightHeight, 3, 1);
                var a = new BoulderJoin(left, 0, right, 0, 0, 0, 0, 0, 0, 0, 0).geometry();
                var b = new BoulderJoin(right, 0, 0, 0, left, 0, 0, 0, 0, 0, 0).geometry();
                for (double y = 0.25; y < Math.min(leftHeight, rightHeight) * 2; y += 0.5) {
                    assertTrue(inside(a, 15.999, y, 8));
                    assertTrue(inside(b, 0.001, y, 8));
                }
            }
        }
    }

    @Test
    void fourWayJunctionHasNoCentralShaft() {
        var northWest = new BoulderJoin(FULL, 0, FULL, FULL, 0, 0, 0, FULL, 0, 0, 0).geometry();
        var northEast = new BoulderJoin(FULL, 0, 0, FULL, FULL, 0, 0, 0, FULL, 0, 0).geometry();
        var southWest = new BoulderJoin(FULL, FULL, FULL, 0, 0, 0, FULL, 0, 0, 0, 0).geometry();
        var southEast = new BoulderJoin(FULL, FULL, 0, 0, FULL, FULL, 0, 0, 0, 0, 0).geometry();
        for (double y = 0.25; y < 16; y += 0.5) {
            assertTrue(inside(northWest, 15.999, y, 15.999));
            assertTrue(inside(northEast, 0.001, y, 15.999));
            assertTrue(inside(southWest, 15.999, y, 0.001));
            assertTrue(inside(southEast, 0.001, y, 0.001));
        }
        assertFalse(inside(northWest, 0.001, 15, 0.001), "The exposed outer corner keeps its bevel.");
    }

    @Test
    void stackedPartsJoinTheirFootprintsWithoutBridgingEmptySpace() {
        int upper = BoulderJoin.piece(4, 3, 2);
        var lowerShape = new BoulderJoin(FULL, 0, 0, 0, 0, 0, 0, 0, 0, upper, 0).geometry();
        var upperShape = new BoulderJoin(upper, 0, 0, 0, 0, 0, 0, 0, 0, 0, FULL).geometry();
        for (int x = 1; x < 16; x++) {
            for (int z = 1; z < 16; z++) {
                if (inside(upperShape, x, 0.001, z)) assertTrue(inside(lowerShape, x, 15.999, z));
            }
        }
        int half = BoulderJoin.piece(4, 0, 0);
        var floating = new BoulderJoin(half, 0, 0, 0, 0, 0, 0, 0, 0, upper, 0).geometry();
        assertEquals(8, floating.stream().mapToDouble(BoulderCatalog.Box::maxY).max().orElseThrow());
    }

    private static boolean inside(List<BoulderCatalog.Box> boxes, double x, double y, double z) {
        return boxes.stream().anyMatch(box -> x >= box.minX() && x <= box.maxX()
                && y >= box.minY() && y <= box.maxY() && z >= box.minZ() && z <= box.maxZ());
    }

    @Test
    void matchingFullHeightStackHasAContinuousChamferWithoutRepeatingLedges() {
        var shape = new BoulderJoin(FULL, 0, 0, 0, 0, 0, 0, 0, 0, FULL, FULL).geometry();
        for (double y = 0.25; y < 16; y += 0.5) {
            assertTrue(inside(shape, 1, y, 8));
            assertFalse(inside(shape, 1, y, 1));
        }
    }
}
