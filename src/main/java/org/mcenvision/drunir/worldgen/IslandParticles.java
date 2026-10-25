package org.mcenvision.drunir.worldgen;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.mcenvision.drunir.DrunirCollection;

public final class IslandParticles {
    private static final DeferredRegister<ParticleType<?>> TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, DrunirCollection.ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHORE_MIST =
            TYPES.register("shore_mist", () -> new SimpleParticleType(false));

    private IslandParticles() {}

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }
}
