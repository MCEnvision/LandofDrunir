package org.mcenvision.drunir.boulder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.mcenvision.drunir.boulder.BoulderCatalog.Box;

/** Extracts the exposed, coplanar rectangles of a union of rock boxes. */
public final class BoulderSurface {
    public enum Side {
        DOWN(1, false), UP(1, true), NORTH(2, false), SOUTH(2, true), WEST(0, false), EAST(0, true);

        private final int axis;
        private final boolean positive;

        Side(int axis, boolean positive) {
            this.axis = axis;
            this.positive = positive;
        }
    }

    public record Face(Side side, Box bounds) {
        public boolean boundary() {
            double coordinate = switch (side.axis) {
                case 0 -> bounds.minX();
                case 1 -> bounds.minY();
                default -> bounds.minZ();
            };
            return coordinate == (side.positive ? 16 : 0);
        }
    }

    private BoulderSurface() {}

    public static List<Face> mesh(List<Box> boxes) {
        if (boxes.isEmpty()) return List.of();
        double[][] coordinates = new double[3][];
        for (int axis = 0; axis < 3; axis++) {
            var values = new TreeSet<Double>();
            for (var box : boxes) {
                values.add(min(box, axis));
                values.add(max(box, axis));
            }
            coordinates[axis] = values.stream().mapToDouble(Double::doubleValue).toArray();
        }
        int[] size = Arrays.stream(coordinates).mapToInt(values -> values.length - 1).toArray();
        var occupied = new boolean[size[0]][size[1]][size[2]];
        for (var box : boxes) {
            int x1 = Arrays.binarySearch(coordinates[0], box.minX());
            int x2 = Arrays.binarySearch(coordinates[0], box.maxX());
            int y1 = Arrays.binarySearch(coordinates[1], box.minY());
            int y2 = Arrays.binarySearch(coordinates[1], box.maxY());
            int z1 = Arrays.binarySearch(coordinates[2], box.minZ());
            int z2 = Arrays.binarySearch(coordinates[2], box.maxZ());
            for (int x = x1; x < x2; x++) {
                for (int y = y1; y < y2; y++) {
                    Arrays.fill(occupied[x][y], z1, z2, true);
                }
            }
        }
        var faces = new ArrayList<Face>();
        for (var side : Side.values()) {
            int axis = side.axis;
            int u = (axis + 1) % 3;
            int v = (axis + 2) % 3;
            for (int plane = 0; plane <= size[axis]; plane++) {
                var exposed = new boolean[size[u]][size[v]];
                int inside = side.positive ? plane - 1 : plane;
                int outside = side.positive ? plane : plane - 1;
                for (int a = 0; a < size[u]; a++) {
                    for (int b = 0; b < size[v]; b++) {
                        exposed[a][b] = occupied(occupied, size, axis, inside, a, b)
                                && !occupied(occupied, size, axis, outside, a, b);
                    }
                }
                for (int a = 0; a < size[u]; a++) {
                    for (int b = 0; b < size[v]; b++) {
                        if (!exposed[a][b]) continue;
                        int endU = a + 1;
                        while (endU < size[u] && exposed[endU][b]) endU++;
                        int endV = b + 1;
                        while (endV < size[v] && strip(exposed, a, endU, endV)) endV++;
                        for (int row = a; row < endU; row++) Arrays.fill(exposed[row], b, endV, false);
                        double[] lower = new double[3];
                        double[] upper = new double[3];
                        lower[axis] = upper[axis] = coordinates[axis][plane];
                        lower[u] = coordinates[u][a];
                        upper[u] = coordinates[u][endU];
                        lower[v] = coordinates[v][b];
                        upper[v] = coordinates[v][endV];
                        faces.add(new Face(side, new Box(lower[0], lower[1], lower[2], upper[0], upper[1], upper[2])));
                    }
                }
            }
        }
        return List.copyOf(faces);
    }

    private static boolean strip(boolean[][] mask, int start, int end, int column) {
        for (int row = start; row < end; row++) if (!mask[row][column]) return false;
        return true;
    }

    private static boolean occupied(boolean[][][] cells, int[] size, int axis, int depth, int a, int b) {
        if (depth < 0 || depth >= size[axis]) return false;
        return switch (axis) {
            case 0 -> cells[depth][a][b];
            case 1 -> cells[b][depth][a];
            default -> cells[a][b][depth];
        };
    }

    private static double min(Box box, int axis) {
        return switch (axis) { case 0 -> box.minX(); case 1 -> box.minY(); default -> box.minZ(); };
    }

    private static double max(Box box, int axis) {
        return switch (axis) { case 0 -> box.maxX(); case 1 -> box.maxY(); default -> box.maxZ(); };
    }
}
