package org.mcenvision.drunir.mixin;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.mcenvision.drunir.worldgen.IslandNoiseAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class IslandCarverMixin {
    @Inject(method = "applyCarvers", at = @At("HEAD"), cancellable = true)
    private void drunir$protectGround(WorldGenRegion level, long seed, RandomState random, BiomeManager biomes,
                                       StructureManager structures, ChunkAccess chunk, GenerationStep.Carving step,
                                       CallbackInfo callback) {
        var context = ((IslandNoiseAccess) (Object) random).drunir$getIslandControl().context();
        var pos = chunk.getPos();
        if (context != null && context.region().intersectsSanctuary(pos.getMinBlockX(), pos.getMinBlockZ(),
                pos.getMaxBlockX(), pos.getMaxBlockZ())) {
            callback.cancel();
        }
    }
}
