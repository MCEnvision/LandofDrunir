package org.mcenvision.drunir.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mcenvision.drunir.DrunirCollection;

public final class IslandFeatures {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, DrunirCollection.ID);

    static {
        FEATURES.register("shore_blend", ShoreBlendFeature::new);
        FEATURES.register("sunken_ship", SunkenShipFeature::new);
        FEATURES.register("island_boulder", IslandBoulderFeature::new);
        FEATURES.register("golden_grove", GoldenGroveFeature::new);
    }

    private IslandFeatures() {}

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
