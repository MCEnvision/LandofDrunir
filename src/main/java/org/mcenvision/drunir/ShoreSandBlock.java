package org.mcenvision.drunir;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class ShoreSandBlock extends Block {
    public static final MapCodec<ShoreSandBlock> CODEC = simpleCodec(ShoreSandBlock::new);
    public static final IntegerProperty TONE = IntegerProperty.create("tone", 0, ShorePalette.LAST_TONE);

    public ShoreSandBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TONE, 0));
    }

    @Override
    public MapCodec<ShoreSandBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TONE);
    }
}
