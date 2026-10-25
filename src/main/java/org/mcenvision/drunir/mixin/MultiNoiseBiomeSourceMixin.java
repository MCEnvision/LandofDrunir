package org.mcenvision.drunir.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.mcenvision.drunir.worldgen.IslandBiomeAccess;
import org.mcenvision.drunir.worldgen.IslandContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin implements IslandBiomeAccess {
    @Unique private volatile IslandContext drunir$islandContext;

    @Override
    public IslandContext drunir$getIslandContext() { return drunir$islandContext; }

    @Override
    public void drunir$setIslandContext(IslandContext context) { drunir$islandContext = context; }

    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("HEAD"), cancellable = true)
    private void drunir$selectIsland(int x, int y, int z, Climate.Sampler sampler,
                                    CallbackInfoReturnable<Holder<Biome>> callback) {
        IslandContext context = drunir$islandContext;
        if (context == null) return;
        Holder<Biome> biome = context.biome(x * 4, z * 4);
        if (biome != null) callback.setReturnValue(biome);
    }
}
