package org.mcenvision.drunir.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public final class GroveLayout {
    public static final int CLEARING_RADIUS = 10;
    private static final int CELL_SIZE = 4;
    private static final int SEARCH_RADIUS = 4;
    public record Site(int x, int z, long seed) {}
    private record Candidate(Site site, double priority, double clearance, boolean eligible) {}

    private GroveLayout() {}

    public static List<Site> sites(IslandRegion region, int chunkX, int chunkZ) {
        var sites = new ArrayList<Site>();
        int minX = chunkX * 16, minZ = chunkZ * 16;
        int firstX = Math.floorDiv(minX, CELL_SIZE), firstZ = Math.floorDiv(minZ, CELL_SIZE);
        for (int cellX = firstX; cellX < firstX + 4; cellX++) {
            for (int cellZ = firstZ; cellZ < firstZ + 4; cellZ++) {
                var candidate = candidate(region, cellX, cellZ);
                if (!candidate.eligible()) continue;
                boolean accepted = true;
                for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS && accepted; dx++) {
                    for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                        if (dx == 0 && dz == 0) continue;
                        var neighbor = candidate(region, cellX + dx, cellZ + dz);
                        if (!neighbor.eligible() || neighbor.priority() > candidate.priority()) continue;
                        if (neighbor.priority() == candidate.priority()
                                && (dx > 0 || dx == 0 && dz > 0)) continue;
                        double distance = Math.hypot(candidate.site().x() - neighbor.site().x(),
                                candidate.site().z() - neighbor.site().z());
                        if (distance < Math.max(candidate.clearance(), neighbor.clearance())) {
                            accepted = false;
                            break;
                        }
                    }
                }
                if (accepted) sites.add(candidate.site());
            }
        }
        return List.copyOf(sites);
    }

    private static Candidate candidate(IslandRegion region, int cellX, int cellZ) {
        var random = new SplittableRandom(region.shapeSeed() ^ cellX * 341873128712L ^ cellZ * 132897987541L);
        int x = cellX * CELL_SIZE + random.nextInt(CELL_SIZE);
        int z = cellZ * CELL_SIZE + random.nextInt(CELL_SIZE);
        double distance = region.distance(x, z);
        double forestRadius = region.coastRadius(x, z) - IslandRegion.FOREST_INSET;
        double outward = Math.clamp((distance - CLEARING_RADIUS) / (forestRadius - CLEARING_RADIUS), 0, 1);
        double phase = (region.shapeSeed() & 65535) / 1000.0;
        double patch = Math.sin(x / 37.0 + phase) * Math.sin(z / 43.0 - phase);
        double clearance = (12 - 7.8 * outward) * (1 + 0.18 * patch);
        double priority = random.nextDouble();
        double clearingFade = Math.clamp((distance - CLEARING_RADIUS) / 48, 0, 1);
        boolean eligible = region.forest(x, z) && distance > CLEARING_RADIUS
                && random.nextDouble() < (0.1 + 0.75 * outward) * clearingFade;
        return new Candidate(new Site(x, z, random.nextLong()), priority, clearance, eligible);
    }

    public static boolean clearsCenter(IslandRegion region, Site site, GoldenTreeShape tree) {
        for (var point : tree.logs().keySet()) {
            if (intersectsClearing(region, site.x() + point.x(), site.z() + point.z())) return false;
        }
        for (var point : tree.leaves().keySet()) {
            if (intersectsClearing(region, site.x() + point.x(), site.z() + point.z())) return false;
        }
        return true;
    }

    private static boolean intersectsClearing(IslandRegion region, int x, int z) {
        double dx = Math.max(0, Math.abs((double) x - region.centerX()) - 0.5);
        double dz = Math.max(0, Math.abs((double) z - region.centerZ()) - 0.5);
        return Math.hypot(dx, dz) <= CLEARING_RADIUS;
    }

    public static double wonder(double forestEdgeDistance) {
        return Math.clamp(forestEdgeDistance / 380, 0, 1);
    }
}
