package org.mcenvision.drunir.mixin;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import org.mcenvision.drunir.worldgen.IslandContext;
import org.mcenvision.drunir.worldgen.IslandNoiseAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SurfaceSystem.class)
public abstract class SurfaceSystemMixin {
    @ModifyVariable(method = "buildSurface", at = @At("HEAD"), argsOnly = true)
    private SurfaceRules.RuleSource drunir$surface(SurfaceRules.RuleSource original, RandomState random,
                                                  BiomeManager manager, Registry<Biome> biomes, boolean legacy,
                                                  WorldGenerationContext generation, ChunkAccess chunk, NoiseChunk noise,
                                                  SurfaceRules.RuleSource rule) {
        IslandContext context = ((IslandNoiseAccess) (Object) random).drunir$getIslandControl().context();
        return context == null ? original : context.surfaceRule();
    }
}
