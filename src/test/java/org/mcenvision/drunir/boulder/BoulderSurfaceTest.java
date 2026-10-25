package org.mcenvision.drunir.boulder;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mcenvision.drunir.boulder.BoulderCatalog.Box;

class BoulderSurfaceTest {
    @Test
    void touchingAndOverlappingBoxesProduceOnlyTheSixExteriorCubeFaces() {
        for (int overlap = 0; overlap <= 4; overlap += 4) {
            var faces = BoulderSurface.mesh(List.of(new Box(0, 0, 0, 8 + overlap, 16, 16),
                    new Box(8, 0, 0, 16, 16, 16)));
            assertEquals(6, faces.size());
            assertTrue(faces.stream().allMatch(BoulderSurface.Face::boundary));
            assertEquals(1536, faces.stream().mapToDouble(face -> area(face.bounds())).sum());
        }
    }

    @Test
    void steppedRockKeepsTheExposedShelfWithoutDrawingItsInternalFloor() {
        var faces = BoulderSurface.mesh(List.of(new Box(0, 0, 0, 16, 8, 16), new Box(0, 8, 0, 8, 16, 16)));
        assertEquals(1408, faces.stream().mapToDouble(face -> area(face.bounds())).sum());
        var shelf = faces.stream().filter(face -> face.side() == BoulderSurface.Side.UP && face.bounds().minY() == 8).toList();
        assertEquals(List.of(new BoulderSurface.Face(BoulderSurface.Side.UP, new Box(8, 8, 0, 16, 8, 16))), shelf);
    }

    @Test
    void fractionalAndConnectedRockFacesAlwaysSeparateSolidFromAir() {
        int full = BoulderJoin.piece(8, 0, 0);
        for (int form = 0; form < 4; form++) {
            for (int layers = 1; layers <= 8; layers++) {
                int center = BoulderJoin.piece(layers, form, 1);
                var boxes = new BoulderJoin(center, full, 0, full, full, 0, 0, 0, full, full, full).geometry();
                for (var face : BoulderSurface.mesh(boxes)) {
                    var b = face.bounds();
                    double x = (b.minX() + b.maxX()) / 2;
                    double y = (b.minY() + b.maxY()) / 2;
                    double z = (b.minZ() + b.maxZ()) / 2;
                    double dx = face.side() == BoulderSurface.Side.EAST ? 0.0001 : face.side() == BoulderSurface.Side.WEST ? -0.0001 : 0;
                    double dy = face.side() == BoulderSurface.Side.UP ? 0.0001 : face.side() == BoulderSurface.Side.DOWN ? -0.0001 : 0;
                    double dz = face.side() == BoulderSurface.Side.SOUTH ? 0.0001 : face.side() == BoulderSurface.Side.NORTH ? -0.0001 : 0;
                    assertTrue(inside(boxes, x - dx, y - dy, z - dz));
                    assertFalse(inside(boxes, x + dx, y + dy, z + dz));
                }
            }
        }
    }

    private static boolean inside(List<Box> boxes, double x, double y, double z) {
        return boxes.stream().anyMatch(b -> x >= b.minX() && x <= b.maxX() && y >= b.minY()
                && y <= b.maxY() && z >= b.minZ() && z <= b.maxZ());
    }

    private static double area(Box box) {
        double x = box.maxX() - box.minX(), y = box.maxY() - box.minY(), z = box.maxZ() - box.minZ();
        return x * y + y * z + x * z;
    }
}
