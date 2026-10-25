package org.mcenvision.drunir.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.mcenvision.drunir.worldgen.IslandBiomeAccess;
import org.mcenvision.drunir.worldgen.IslandContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(BiomeSource.class)
public abstract class BiomeSourceMixin {
    @Inject(method = "possibleBiomes", at = @At("HEAD"), cancellable = true)
    private void drunir$includeIsland(CallbackInfoReturnable<Set<Holder<Biome>>> callback) {
        if ((Object) this instanceof IslandBiomeAccess access) {
            IslandContext context = access.drunir$getIslandContext();
            if (context != null) callback.setReturnValue(context.possibleBiomes());
        }
    }
}
