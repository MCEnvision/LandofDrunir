package org.mcenvision.drunir;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public final class BoulderBlock extends SlabBlock {
    public static final MapCodec<BoulderBlock> CODEC = simpleCodec(BoulderBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape[] LOWER = new VoxelShape[4];
    private static final VoxelShape[] UPPER = new VoxelShape[4];
    private static final VoxelShape[] STACKED = new VoxelShape[4];

    static {
        List<List<Double>> boxes = DrunirCollection.CONTENT.boulderShape();
        for (int turn = 0; turn < 4; turn++) {
            VoxelShape shape = Shapes.empty();
            for (List<Double> box : boxes) {
                double x1 = box.get(0), z1 = box.get(2), x2 = box.get(3), z2 = box.get(5);
                for (int rotation = 0; rotation < turn; rotation++) {
                    double nextX1 = 16 - z2, nextX2 = 16 - z1;
                    z1 = x1;
                    z2 = x2;
                    x1 = nextX1;
                    x2 = nextX2;
                }
                shape = Shapes.or(shape, Block.box(x1, box.get(1), z1, x2, box.get(4), z2));
            }
            LOWER[turn] = shape.optimize();
            UPPER[turn] = shape.move(0, 0.5, 0);
            STACKED[turn] = Shapes.or(LOWER[turn], UPPER[turn]).optimize();
        }
    }

    public BoulderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BoulderBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (context.getLevel().getBlockState(context.getClickedPos()).is(this)) {
            return state;
        }
        return state.setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int turn = switch (state.getValue(FACING)) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        return switch (state.getValue(TYPE)) {
            case BOTTOM -> LOWER[turn];
            case TOP -> UPPER[turn];
            case DOUBLE -> STACKED[turn];
        };
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }
}
