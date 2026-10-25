package org.mcenvision.drunir.worldgen;

import java.util.SplittableRandom;
import java.util.function.Predicate;

public record IslandRegion(int centerX, int centerZ, long shapeSeed) {
    public static final int RADIUS = 256;
    public static final int FOREST_INSET = 32;
    public static final int OCEAN_RADIUS = 512;
    public static final int BLEND_RADIUS = 768;

    public static IslandRegion fromSeed(long seed) {
        return candidate(seed, 0);
    }

    public static IslandRegion findUnexplored(long seed, Predicate<IslandRegion> available) {
        for (int attempt = 0; attempt < 128; attempt++) {
            IslandRegion region = candidate(seed, attempt);
            if (available.test(region)) return region;
        }
        throw new IllegalStateException("No unexplored region was available for Yellow Tree Forest. Existing terrain was preserved.");
    }

    private static IslandRegion candidate(long seed, int attempt) {
        SplittableRandom random = new SplittableRandom(seed ^ 0x6A09E667F3BCC909L ^ (0x9E3779B97F4A7C15L * attempt));
        double angle = random.nextDouble() * Math.PI * 2;
        int distance = 4096 + random.nextInt(2048) + attempt * 2048;
        return new IslandRegion((int) Math.round(Math.cos(angle) * distance) & ~15,
                (int) Math.round(Math.sin(angle) * distance) & ~15, random.nextLong());
    }

    public boolean affects(int x, int z) {
        long dx = (long) x - centerX;
        long dz = (long) z - centerZ;
        return Math.abs(dx) < BLEND_RADIUS && Math.abs(dz) < BLEND_RADIUS
                && dx * dx + dz * dz < (long) BLEND_RADIUS * BLEND_RADIUS;
    }

    public double coastRadius(int x, int z) {
        double angle = Math.atan2((double) z - centerZ, (double) x - centerX);
        double phase = phase();
        return RADIUS * (1 + 0.06 * Math.sin(3 * angle + phase) + 0.04 * Math.sin(5 * angle - phase))
                + 9 * Math.sin(11 * angle + phase) + 5 * Math.sin(17 * angle - phase);
    }

    public double distance(int x, int z) {
        return Math.hypot((double) x - centerX, (double) z - centerZ);
    }

    public boolean forest(int x, int z) {
        if (!affects(x, z)) return false;
        return forestEdgeDistance(x, z) > 0;
    }

    public double forestEdgeDistance(int x, int z) {
        return coastRadius(x, z) - FOREST_INSET - distance(x, z);
    }

    public boolean beach(int x, int z) {
        return affects(x, z) && distance(x, z) < coastRadius(x, z) + 96;
    }

    public double surfaceHeight(int x, int z) {
        double dx = (double) x - centerX;
        double dz = (double) z - centerZ;
        double inland = coastRadius(x, z) - distance(x, z);
        double hills = 77 + 5 * Math.sin(dx / 91 + phase()) + 3 * Math.sin(dz / 67 - phase());
        double height;
        if (inland >= 112) {
            height = hills;
        } else if (inland >= 0) {
            height = hermite(hills, 60.5, -5.6, -20.16, (112 - inland) / 112);
        } else if (inland > -128) {
            height = hermite(60.5, 39, -23.04, 0, -inland / 128);
        } else {
            return 39;
        }
        double detail = 2.0 * Math.sin(dx / 19 + phase()) * Math.sin(dz / 23 - phase());
        double fade = inland >= 0 ? 1 : 1 - smooth(Math.min(1, -inland / 128));
        return height + detail * fade;
    }

    private static double hermite(double start, double end, double startSlope, double endSlope, double t) {
        double square = t * t;
        double cube = square * t;
        return (2 * cube - 3 * square + 1) * start + (cube - 2 * square + t) * startSlope
                + (-2 * cube + 3 * square) * end + (cube - square) * endSlope;
    }

    public boolean sanctuary(int x, int z) {
        return distance(x, z) < OCEAN_RADIUS;
    }

    public boolean intersectsSanctuary(int minX, int minZ, int maxX, int maxZ) {
        int x = Math.clamp(centerX, minX, maxX);
        int z = Math.clamp(centerZ, minZ, maxZ);
        return distance(x, z) < OCEAN_RADIUS;
    }

    public double influence(int x, int z) {
        double distance = distance(x, z);
        if (distance <= OCEAN_RADIUS) return 1;
        if (distance >= BLEND_RADIUS) return 0;
        return 1 - smooth((distance - OCEAN_RADIUS) / (BLEND_RADIUS - OCEAN_RADIUS));
    }

    private double phase() {
        return (shapeSeed & 65535L) * (Math.PI * 2 / 65536);
    }

    private static double smooth(double value) { return value * value * (3 - 2 * value); }
}
