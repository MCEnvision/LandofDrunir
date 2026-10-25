package org.mcenvision.drunir;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;

public final class DecorativePlantBlock extends BushBlock {
    public static final MapCodec<DecorativePlantBlock> CODEC = simpleCodec(DecorativePlantBlock::new);

    public DecorativePlantBlock(Properties properties) { super(properties); }

    @Override
    public MapCodec<DecorativePlantBlock> codec() { return CODEC; }
}
