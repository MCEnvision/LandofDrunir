package org.mcenvision.drunir.worldgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

public final class BoulderFormation {
    public static final int VARIANT_COUNT = 128;
    private static final int[][] DIRECTIONS = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    private static final List<List<Cell>> PROFILES = profiles();

    public enum Part { FULL, SLAB, STAIRS }
    public record Cell(int x, int y, int z, Part part, int facing) {}

    private BoulderFormation() {}

    public static List<Cell> create(long seed) {
        var random = new SplittableRandom(seed);
        return rotate(variant(random.nextInt(VARIANT_COUNT)), random.nextInt(4));
    }

    public static List<Cell> variant(int index) {
        if (index < 0 || index >= VARIANT_COUNT) throw new IllegalArgumentException("Unknown rock profile: " + index);
        return PROFILES.get(index);
    }

    private static List<List<Cell>> profiles() {
        var profiles = new ArrayList<List<Cell>>();
        var silhouettes = new HashSet<String>();
        var random = new SplittableRandom(0x6A09E667F3BCC909L);
        for (int attempt = 0; attempt < 4096 && profiles.size() < VARIANT_COUNT; attempt++) {
            int[][] heights = new int[3][3];
            heights[1][1] = random.nextInt(3, 5);
            for (var direction : DIRECTIONS) heights[1 + direction[0]][1 + direction[1]] = random.nextInt(3);
            int shoulder = random.nextInt(4);
            heights[1 + DIRECTIONS[shoulder][0]][1 + DIRECTIONS[shoulder][1]] = 2;
            for (int x : new int[]{0, 2}) {
                for (int z : new int[]{0, 2}) {
                    if (heights[x][1] > 0 || heights[1][z] > 0) heights[x][z] = random.nextInt(2);
                }
            }
            var cells = columns(heights);
            String signature = signature(cells);
            for (int turn = 1; turn < 4; turn++) {
                String rotated = signature(rotate(cells, turn));
                if (rotated.compareTo(signature) < 0) signature = rotated;
            }
            if (silhouettes.add(signature)) profiles.add(cells);
        }
        if (profiles.size() != VARIANT_COUNT) throw new IllegalStateException("Insufficient distinct rock profiles");
        return List.copyOf(profiles);
    }

    private static String signature(List<Cell> cells) {
        return cells.stream().map(cell -> cell.x() + "," + cell.y() + "," + cell.z())
                .sorted().collect(Collectors.joining(";"));
    }

    private static List<Cell> columns(int[][] heights) {
        var cells = new ArrayList<Cell>();
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                int columnHeight = heights[x][z];
                int facing = 0;
                int highestNeighbor = columnHeight;
                for (int direction = 0; direction < DIRECTIONS.length; direction++) {
                    int nx = x + DIRECTIONS[direction][0], nz = z + DIRECTIONS[direction][1];
                    if (nx >= 0 && nx < 3 && nz >= 0 && nz < 3 && heights[nx][nz] > highestNeighbor) {
                        highestNeighbor = heights[nx][nz];
                        facing = direction;
                    }
                }
                for (int y = 0; y < columnHeight; y++) {
                    Part part = y == 0 || y < columnHeight - 1 ? Part.FULL
                            : highestNeighbor > columnHeight ? Part.STAIRS : Part.SLAB;
                    cells.add(new Cell(x - 1, y, z - 1, part, part == Part.STAIRS ? facing : 0));
                }
            }
        }
        return List.copyOf(cells);
    }

    public static List<Cell> rotate(List<Cell> cells, int quarterTurns) {
        int turns = Math.floorMod(quarterTurns, 4);
        return cells.stream().map(cell -> {
            int x = cell.x(), z = cell.z();
            for (int turn = 0; turn < turns; turn++) { int oldX = x; x = -z; z = oldX; }
            return new Cell(x, cell.y(), z, cell.part(), cell.part() == Part.STAIRS ? (cell.facing() + turns) % 4 : 0);
        }).toList();
    }
}
