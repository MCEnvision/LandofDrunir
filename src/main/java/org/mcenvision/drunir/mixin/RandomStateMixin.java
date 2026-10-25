package org.mcenvision.drunir.mixin;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.mcenvision.drunir.worldgen.IslandControl;
import org.mcenvision.drunir.worldgen.IslandDensityFunction;
import org.mcenvision.drunir.worldgen.IslandNoiseAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomState.class)
public abstract class RandomStateMixin implements IslandNoiseAccess {
    @Shadow @Final @Mutable private NoiseRouter router;
    @Unique private IslandControl drunir$islandControl;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void drunir$wrapDensity(NoiseGeneratorSettings settings, HolderGetter<NormalNoise.NoiseParameters> noises,
                                   long seed, CallbackInfo callback) {
        drunir$islandControl = new IslandControl();
        router = IslandDensityFunction.wrap(router, drunir$islandControl);
    }

    @Override
    public IslandControl drunir$getIslandControl() { return drunir$islandControl; }
}
