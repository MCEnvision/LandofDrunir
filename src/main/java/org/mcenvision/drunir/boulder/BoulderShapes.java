package org.mcenvision.drunir.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BoulderShapes {
    private static final int CACHE_LIMIT = 512;
    private static final Map<BoulderJoin, VoxelShape> CACHE = new LinkedHashMap<>(64, 0.75F, true);

    private BoulderShapes() {}

    public static BoulderJoin neighborhood(BlockGetter level, BlockPos pos, BlockState state) {
        var cursor = new BlockPos.MutableBlockPos();
        return new BoulderJoin(encode(state), at(level, pos, state, cursor, 0, 0, -1),
                at(level, pos, state, cursor, 1, 0, 0), at(level, pos, state, cursor, 0, 0, 1),
                at(level, pos, state, cursor, -1, 0, 0), at(level, pos, state, cursor, -1, 0, -1),
                at(level, pos, state, cursor, 1, 0, -1), at(level, pos, state, cursor, 1, 0, 1),
                at(level, pos, state, cursor, -1, 0, 1), at(level, pos, state, cursor, 0, 1, 0),
                at(level, pos, state, cursor, 0, -1, 0));
    }

    private static int at(BlockGetter level, BlockPos pos, BlockState state, BlockPos.MutableBlockPos cursor,
                          int x, int y, int z) {
        cursor.setWithOffset(pos, x, y, z);
        var neighbor = level.getBlockState(cursor);
        return neighbor.is(state.getBlock()) ? encode(neighbor) : 0;
    }

    public static int encode(BlockState state) {
        int turn = switch (state.getValue(BoulderPieceBlock.FACING)) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        return BoulderJoin.piece(state.getValue(BoulderPieceBlock.LAYERS), state.getValue(BoulderPieceBlock.FORM), turn);
    }

    public static VoxelShape standalone(BlockState state) {
        return shape(new BoulderJoin(encode(state), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    }

    public static synchronized VoxelShape shape(BoulderJoin join) {
        var cached = CACHE.get(join);
        if (cached != null) {
            return cached;
        }
        VoxelShape shape = Shapes.empty();
        for (var box : join.geometry()) {
            shape = Shapes.or(shape, Block.box(box.minX(), box.minY(), box.minZ(),
                    box.maxX(), box.maxY(), box.maxZ()));
        }
        shape = shape.optimize();
        if (CACHE.size() >= CACHE_LIMIT) {
            CACHE.remove(CACHE.keySet().iterator().next());
        }
        CACHE.put(join, shape);
        return shape;
    }
}
