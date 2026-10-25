package org.mcenvision.drunir.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.mcenvision.drunir.worldgen.IslandNoiseAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(Structure.class)
public abstract class IslandStructureMixin {
    @Inject(method = "generate", at = @At("RETURN"), cancellable = true)
    private void drunir$excludeStructures(RegistryAccess registries, ChunkGenerator generator, BiomeSource biomes,
                                          RandomState random, StructureTemplateManager templates, long seed,
                                          ChunkPos chunk, int references, LevelHeightAccessor height,
                                          Predicate<Holder<Biome>> allowedBiomes, CallbackInfoReturnable<StructureStart> callback) {
        var context = ((IslandNoiseAccess) (Object) random).drunir$getIslandControl().context();
        var start = callback.getReturnValue();
        if (context != null && start.isValid()) {
            var box = start.getBoundingBox();
            if (context.region().intersectsSanctuary(box.minX(), box.minZ(), box.maxX(), box.maxZ())) {
                callback.setReturnValue(StructureStart.INVALID_START);
            }
        }
    }
}
