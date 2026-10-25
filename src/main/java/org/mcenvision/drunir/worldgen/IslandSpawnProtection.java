package org.mcenvision.drunir.worldgen;

import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.mcenvision.drunir.DrunirCollection;

@EventBusSubscriber(modid = DrunirCollection.ID)
public final class IslandSpawnProtection {
    private IslandSpawnProtection() {}

    private static boolean blocked(ServerLevelAccessor level, double x, double z, MobSpawnType type) {
        if (type == MobSpawnType.COMMAND || type == MobSpawnType.SPAWN_EGG || type == MobSpawnType.DISPENSER
                || type == MobSpawnType.BUCKET || type == MobSpawnType.BREEDING || type == MobSpawnType.CONVERSION) {
            return false;
        }
        var context = IslandWorldgen.context(level.getLevel());
        return context != null && context.region().sanctuary((int) Math.floor(x), (int) Math.floor(z));
    }

    @SubscribeEvent
    public static void placement(MobSpawnEvent.SpawnPlacementCheck event) {
        if (blocked(event.getLevel(), event.getPos().getX(), event.getPos().getZ(), event.getSpawnType())) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }

    @SubscribeEvent
    public static void position(MobSpawnEvent.PositionCheck event) {
        if (blocked(event.getLevel(), event.getX(), event.getZ(), event.getSpawnType())) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }

    @SubscribeEvent
    public static void finalizeSpawn(FinalizeSpawnEvent event) {
        if (blocked(event.getLevel(), event.getX(), event.getZ(), event.getSpawnType())) {
            event.setSpawnCancelled(true);
        }
    }
}
