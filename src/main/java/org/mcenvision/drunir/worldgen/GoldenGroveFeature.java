package org.mcenvision.drunir.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.mcenvision.drunir.DrunirCollection;

public final class GoldenGroveFeature extends Feature<NoneFeatureConfiguration> {
    public GoldenGroveFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placement) {
        var level = placement.level();
        var context = IslandWorldgen.context(level.getLevel());
        if (context == null) return false;
        var region = context.region();
        var grass = DrunirCollection.BLOCK_ENTRIES.get("yellow_grass_block").get();
        var leaf = DrunirCollection.BLOCK_ENTRIES.get("yellow_birch_leaves").get().defaultBlockState();
        boolean placed = false;
        for (var site : GroveLayout.sites(region, placement.origin().getX() >> 4, placement.origin().getZ() >> 4)) {
            if (!region.forest(site.x(), site.z())) continue;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, site.x(), site.z());
            var origin = new BlockPos(site.x(), y, site.z());
            if (!level.getBlockState(origin.below()).is(grass)) continue;
            var tree = GoldenTreeShape.create(site.seed(), GroveLayout.wonder(region.forestEdgeDistance(site.x(), site.z())));
            if (!GroveLayout.clearsCenter(region, site, tree)) continue;
            int lift = canopyLift(level, origin, tree);
            if (lift < 0) continue;
            tree = tree.raisedBy(lift);
            var cells = new java.util.HashSet<>(tree.logs().keySet());
            cells.addAll(tree.leaves().keySet());
            boolean clear = true;
            for (var cell : cells) {
                var pos = origin.offset(cell.x(), cell.y(), cell.z());
                var state = level.getBlockState(pos);
                if (!tree.logs().containsKey(cell) && state.is(BlockTags.LOGS)) continue;
                if (level.isOutsideBuildHeight(pos) || !state.getFluidState().isEmpty()
                        || !(state.isAir() || state.is(BlockTags.REPLACEABLE_BY_TREES))) { clear = false; break; }
            }
            if (!clear) continue;
            level.setBlock(origin.below(), Blocks.DIRT.defaultBlockState(), 2);
            tree.logs().forEach((cell, axis) -> level.setBlock(origin.offset(cell.x(), cell.y(), cell.z()),
                    Blocks.BIRCH_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS,
                            axis == 'x' ? Direction.Axis.X : axis == 'z' ? Direction.Axis.Z : Direction.Axis.Y), 2));
            tree.leaves().forEach((cell, distance) -> {
                var pos = origin.offset(cell.x(), cell.y(), cell.z());
                if (!level.getBlockState(pos).is(BlockTags.LOGS)) level.setBlock(pos, leaf.setValue(LeavesBlock.DISTANCE, distance), 2);
            });
            placed = true;
        }
        return placed;
    }

    private static int canopyLift(net.minecraft.world.level.WorldGenLevel level, BlockPos origin, GoldenTreeShape tree) {
        var lowestLeaves = new java.util.HashMap<GoldenTreeShape.Point, Integer>();
        tree.leaves().keySet().forEach(point -> lowestLeaves.merge(new GoldenTreeShape.Point(point.x(), 0, point.z()),
                point.y(), Math::min));
        int lift = 0;
        var cursor = new BlockPos.MutableBlockPos();
        for (var column : lowestLeaves.entrySet()) {
            var offset = column.getKey();
            boolean foundGround = false;
            for (int y = origin.getY() + 8; y >= origin.getY() - 8; y--) {
                cursor.set(origin.getX() + offset.x(), y, origin.getZ() + offset.z());
                if (!level.getBlockState(cursor).is(BlockTags.DIRT)) continue;
                lift = Math.max(lift, y + 4 - (origin.getY() + column.getValue()));
                foundGround = true;
                break;
            }
            if (!foundGround) return -1;
        }
        return lift;
    }

}
