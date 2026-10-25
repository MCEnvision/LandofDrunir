package org.mcenvision.drunir.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.mcenvision.drunir.client.IslandAtmosphere;
import org.mcenvision.drunir.client.MysticNavigation;
import org.mcenvision.drunir.worldgen.IslandVeil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassItemPropertyFunction.class)
public abstract class CompassVeilMixin {
    @Inject(method = "getCompassRotation", at = @At("HEAD"), cancellable = true)
    private void drunir$spin(ItemStack stack, ClientLevel level, int seed, Entity entity, CallbackInfoReturnable<Float> result) {
        if (IslandVeil.contains(IslandAtmosphere.region(), entity.getBlockX(), entity.getBlockZ())) {
            result.setReturnValue(MysticNavigation.compass(level.getGameTime()
                    + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false), seed));
        }
    }
}
