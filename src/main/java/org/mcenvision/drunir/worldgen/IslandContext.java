package org.mcenvision.drunir.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.Set;

public record IslandContext(IslandRegion region, Holder<Biome> forest, Holder<Biome> beach,
                            Holder<Biome> ocean, Set<Holder<Biome>> possibleBiomes, SurfaceRules.RuleSource surfaceRule,
                            java.util.List<IslandWreckSites.Site> wrecks) {
    public Holder<Biome> biome(int x, int z) {
        if (!region.affects(x, z)) return null;
        if (region.forest(x, z)) return forest;
        if (region.beach(x, z)) return beach;
        return region.distance(x, z) < IslandRegion.OCEAN_RADIUS ? ocean : null;
    }
}
