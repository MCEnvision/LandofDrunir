package org.mcenvision.drunir.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public final class IslandWreckSites {
    public record Site(int x, int z, int variant, int turns) {}

    private IslandWreckSites() {}

    public static List<Site> create(IslandRegion region) {
        var random = new SplittableRandom(region.shapeSeed() ^ 0x3C6EF372FE94F82BL);
        double phase = random.nextDouble() * Math.PI * 2;
        var sites = new ArrayList<Site>();
        for (int index = 0; index < 6; index++) {
            double angle = phase + index * Math.PI / 3 + random.nextDouble(-0.12, 0.12);
            int sampleX = region.centerX() + (int) Math.round(Math.cos(angle) * IslandRegion.RADIUS);
            int sampleZ = region.centerZ() + (int) Math.round(Math.sin(angle) * IslandRegion.RADIUS);
            double distance = region.coastRadius(sampleX, sampleZ) + random.nextInt(64, 89);
            sites.add(new Site(region.centerX() + (int) Math.round(Math.cos(angle) * distance),
                    region.centerZ() + (int) Math.round(Math.sin(angle) * distance), random.nextInt(4), random.nextInt(4)));
        }
        return List.copyOf(sites);
    }
}
