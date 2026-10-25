package org.mcenvision.drunir.worldgen;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;

public record GoldenTreeShape(Form form, Map<Point, Character> logs, Map<Point, Integer> leaves) {
    public enum Form { ROUNDED, BRANCHING }
    public record Point(int x, int y, int z) {
        public Point offset(int dx, int dy, int dz) { return new Point(x + dx, y + dy, z + dz); }
    }
    private static final int[][] NEIGHBORS = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

    public static GoldenTreeShape create(long seed, double wonder) {
        var random = new SplittableRandom(seed);
        wonder = Math.clamp(wonder, 0, 1);
        Form form = random.nextDouble() < 0.15 + 0.55 * wonder ? Form.BRANCHING : Form.ROUNDED;
        int profile = random.nextInt(4);
        int size = random.nextInt(10);
        int height;
        if (size < 3) height = form == Form.ROUNDED ? random.nextInt(5, 8) : random.nextInt(7, 10);
        else if (size < 8) height = form == Form.ROUNDED ? random.nextInt(9, 13) : random.nextInt(10, 15);
        else height = form == Form.ROUNDED ? random.nextInt(14, 20) : random.nextInt(15, 23);
        var logs = new LinkedHashMap<Point, Character>();
        var foliage = new HashSet<Point>();
        int turn = random.nextInt(4);
        int dx = turn == 0 ? 1 : turn == 2 ? -1 : 0;
        int dz = turn == 1 ? 1 : turn == 3 ? -1 : 0;
        Point tip = new Point(0, 0, 0);
        for (int y = 0; y < height; y++) {
            tip = new Point(tip.x(), y, tip.z());
            logs.put(tip, 'y');
            if (profile == 3 && y > height / 2 && y % 2 == 0) {
                tip = tip.offset(dx, 0, dz);
                logs.put(tip, dx == 0 ? 'z' : 'x');
            }
        }
        if (form == Form.ROUNDED) {
            if (profile < 2) {
                int radius = profile == 1 ? 3 : 2;
                layer(foliage, tip.offset(0, -2, 0), radius, random);
                layer(foliage, tip.offset(0, -1, 0), radius, random);
                layer(foliage, tip, 1, random);
                layer(foliage, tip.offset(0, 1, 0), 1, random);
                foliage.add(tip.offset(0, 2, 0));
            } else {
                puff(foliage, tip.offset(0, 1, 0), 2.6 + random.nextDouble(), 2.5);
                if (profile == 3) puff(foliage, tip.offset(-dx, -2, -dz), 2.2, 1.5);
            }
        } else {
            boolean umbrella = profile % 2 == 1;
            puff(foliage, tip.offset(0, 1, 0), 2.6, umbrella ? 1.5 : 2);
            int arms = 2 + profile % 3;
            Point fork = new Point(tip.x(), height - 4, tip.z());
            if (profile == 3) {
                fork = logs.keySet().stream().filter(point -> point.y() == height - 4)
                        .reduce((first, last) -> last).orElseThrow();
            }
            for (int arm = 0; arm < arms; arm++) {
                int direction = (turn + (arms == 2 ? arm * 2 : arm)) % 4;
                int bx = direction == 0 ? 1 : direction == 2 ? -1 : 0;
                int bz = direction == 1 ? 1 : direction == 3 ? -1 : 0;
                Point end = branch(logs, fork, bx, bz, random.nextInt(2, 4));
                puff(foliage, end.offset(0, 1, 0), umbrella ? 3 : 2.5, umbrella ? 1.5 : 2.2);
            }
        }
        for (Point log : logs.keySet()) {
            if (!logs.containsKey(log.offset(0, 1, 0))) foliage.add(log.offset(0, 1, 0));
        }
        foliage.removeAll(logs.keySet());
        var distances = new HashMap<Point, Integer>();
        var queue = new ArrayDeque<Point>(logs.keySet());
        while (!queue.isEmpty()) {
            Point point = queue.remove();
            int distance = logs.containsKey(point) ? 0 : distances.get(point);
            if (distance == 6) continue;
            for (var offset : NEIGHBORS) {
                Point next = point.offset(offset[0], offset[1], offset[2]);
                if (foliage.contains(next) && distances.putIfAbsent(next, distance + 1) == null) queue.add(next);
            }
        }
        var tree = new GoldenTreeShape(form, Map.copyOf(logs), Map.copyOf(distances));
        int lowestLeaf = distances.keySet().stream().mapToInt(Point::y).min().orElseThrow();
        return tree.raisedBy(Math.max(0, 3 - lowestLeaf));
    }

    public GoldenTreeShape raisedBy(int blocks) {
        if (blocks < 0) throw new IllegalArgumentException("Canopy lift must not be negative");
        if (blocks == 0) return this;
        var raisedLogs = new HashMap<Point, Character>();
        var raisedLeaves = new HashMap<Point, Integer>();
        logs.forEach((point, axis) -> raisedLogs.put(point.offset(0, blocks, 0), axis));
        leaves.forEach((point, distance) -> raisedLeaves.put(point.offset(0, blocks, 0), distance));
        for (int y = 0; y < blocks; y++) raisedLogs.put(new Point(0, y, 0), 'y');
        return new GoldenTreeShape(form, Map.copyOf(raisedLogs), Map.copyOf(raisedLeaves));
    }

    private static Point branch(Map<Point, Character> logs, Point start, int dx, int dz, int length) {
        Point end = start;
        for (int step = 0; step < length; step++) {
            end = end.offset(dx, 0, dz);
            logs.put(end, dx == 0 ? 'z' : 'x');
            end = end.offset(0, 1, 0);
            logs.put(end, 'y');
        }
        return end;
    }

    private static void layer(Set<Point> leaves, Point center, int radius, SplittableRandom random) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.abs(x) == radius && Math.abs(z) == radius && random.nextBoolean()) continue;
                leaves.add(center.offset(x, 0, z));
            }
        }
    }

    private static void puff(Set<Point> leaves, Point center, double radius, double height) {
        int reach = (int) Math.ceil(radius);
        for (int x = -reach; x <= reach; x++) {
            for (int z = -reach; z <= reach; z++) {
                for (int y = -(int) height; y <= height; y++) {
                    if (x * x / (radius * radius) + z * z / (radius * radius) + y * y / (height * height) <= 1.05) {
                        leaves.add(center.offset(x, y, z));
                    }
                }
            }
        }
    }
}
