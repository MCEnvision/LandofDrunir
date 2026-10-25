package org.mcenvision.drunir;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class HollowLogBlock extends RotatedPillarBlock {
    private static final VoxelShape Y_SHAPE = Shapes.or(Block.box(0, 0, 0, 2, 16, 16),
            Block.box(14, 0, 0, 16, 16, 16), Block.box(2, 0, 0, 14, 16, 2), Block.box(2, 0, 14, 14, 16, 16));
    private static final VoxelShape X_SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 14, 0, 16, 16, 16), Block.box(0, 2, 0, 16, 14, 2), Block.box(0, 2, 14, 16, 14, 16));
    private static final VoxelShape Z_SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 14, 0, 16, 16, 16), Block.box(0, 2, 0, 2, 14, 16), Block.box(14, 2, 0, 16, 14, 16));

    public HollowLogBlock(Properties properties) { super(properties); }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos position, CollisionContext context) {
        Direction.Axis axis = state.getValue(AXIS);
        return axis == Direction.Axis.X ? X_SHAPE : axis == Direction.Axis.Z ? Z_SHAPE : Y_SHAPE;
    }
}
