package org.mcenvision.drunir.worldgen;

import org.mcenvision.drunir.ShorePalette;

public final class IslandVeil {
    public static final double GRASS_FADE = 16;

    private IslandVeil() {}

    public static boolean contains(IslandRegion region, int x, int z) {
        return region != null && region.distance(x, z) < IslandRegion.OCEAN_RADIUS;
    }

    public static float fog(IslandRegion region, int x, int z) {
        if (!contains(region, x, z)) return 0;
        double outer = Math.clamp((IslandRegion.OCEAN_RADIUS - region.distance(x, z)) / 16, 0, 1);
        double inland = Math.clamp((region.forestEdgeDistance(x, z) - ShorePalette.HALF_WIDTH) / GRASS_FADE, 0, 1);
        return (float) (outer * (1 - inland * inland * (3 - 2 * inland)));
    }
}
