package org.mcenvision.drunir.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.mcenvision.drunir.DrunirCollection;
import org.mcenvision.drunir.ShorePalette;
import org.mcenvision.drunir.ShoreSandBlock;

public final class ShoreBlendFeature extends Feature<NoneFeatureConfiguration> {
    public ShoreBlendFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placement) {
        var level = placement.level();
        IslandContext context = IslandWorldgen.context(level.getLevel());
        if (context == null) {
            return false;
        }
        var grass = DrunirCollection.BLOCK_ENTRIES.get("yellow_grass_block").get();
        var shore = DrunirCollection.BLOCK_ENTRIES.get("golden_shore_sand").get();
        IslandRegion region = context.region();
        int minX = placement.origin().getX() & ~15;
        int minZ = placement.origin().getZ() & ~15;
        var pos = new BlockPos.MutableBlockPos();
        boolean placed = false;
        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                double inland = region.forestEdgeDistance(x, z);
                if (Math.abs(inland) > ShorePalette.HALF_WIDTH) {
                    continue;
                }
                int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
                if (y < 63) {
                    continue;
                }
                pos.set(x, y, z);
                var surface = level.getBlockState(pos);
                if (surface.is(grass) || surface.is(Blocks.SAND)) {
                    level.setBlock(pos, shore.defaultBlockState().setValue(ShoreSandBlock.TONE,
                            ShorePalette.tone(inland, x, z)), 2);
                    placed = true;
                }
            }
        }
        return placed;
    }
}
