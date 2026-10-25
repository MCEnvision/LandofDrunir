package org.mcenvision.drunir.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.mcenvision.drunir.DrunirCollection;
import org.mcenvision.drunir.boulder.BoulderCatalog;

public final class IslandBoulderFeature extends Feature<NoneFeatureConfiguration> {
    public IslandBoulderFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placement) {
        var level = placement.level();
        var origin = placement.origin().below();
        var context = IslandWorldgen.context(level.getLevel());
        if (context == null || !context.region().forest(origin.getX(), origin.getZ())) return false;
        var cells = BoulderFormation.create(placement.random().nextLong());
        var grass = DrunirCollection.BLOCK_ENTRIES.get("yellow_grass_block").get();
        var shore = DrunirCollection.BLOCK_ENTRIES.get("golden_shore_sand").get();
        var cursor = new BlockPos.MutableBlockPos();
        for (var cell : cells) {
            cursor.setWithOffset(origin, cell.x(), cell.y(), cell.z());
            if (cell.y() == 0) {
                int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, cursor.getX(), cursor.getZ()) - 1;
                if (Math.abs(floor - origin.getY()) > 1) return false;
                var ground = level.getBlockState(cursor.below());
                if (!ground.is(Blocks.DIRT) && !ground.is(grass) && !ground.is(shore)) return false;
            }
            var state = level.getBlockState(cursor);
            if (!state.getFluidState().isEmpty() || !(state.canBeReplaced() || state.is(Blocks.DIRT)
                    || state.is(grass) || state.is(shore))) return false;
        }
        var materials = BoulderCatalog.INSTANCE.materials();
        int weight = materials.stream().mapToInt(BoulderCatalog.Material::weight).sum();
        int roll = placement.random().nextInt(weight);
        var material = materials.getFirst();
        for (var candidate : materials) {
            roll -= candidate.weight();
            if (roll < 0) { material = candidate; break; }
        }
        var stone = DrunirCollection.BLOCK_ENTRIES.get(material.id()).get().defaultBlockState();
        var slab = DrunirCollection.BLOCK_ENTRIES.get(material.id() + "_rock_slab").get().defaultBlockState();
        var stairs = DrunirCollection.BLOCK_ENTRIES.get(material.id() + "_rock_stairs").get().defaultBlockState();
        Direction[] directions = {Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};
        for (var cell : cells) {
            cursor.setWithOffset(origin, cell.x(), cell.y(), cell.z());
            var state = switch (cell.part()) {
                case FULL -> stone;
                case SLAB -> slab;
                case STAIRS -> stairs.setValue(StairBlock.FACING, directions[cell.facing()]);
            };
            level.setBlock(cursor, state, 2);
        }
        for (var cell : cells) {
            if (cell.part() != BoulderFormation.Part.STAIRS) continue;
            cursor.setWithOffset(origin, cell.x(), cell.y(), cell.z());
            level.setBlock(cursor, Block.updateFromNeighbourShapes(level.getBlockState(cursor), level, cursor), 2);
        }
        return true;
    }
}
