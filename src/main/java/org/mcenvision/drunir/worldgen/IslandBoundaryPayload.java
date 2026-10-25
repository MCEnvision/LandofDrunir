package org.mcenvision.drunir.worldgen;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.mcenvision.drunir.DrunirCollection;

@EventBusSubscriber(modid = DrunirCollection.ID)
public record IslandBoundaryPayload(boolean active, int centerX, int centerZ, long shapeSeed) implements CustomPacketPayload {
    public static final Type<IslandBoundaryPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(DrunirCollection.ID, "island_boundary"));
    public static final StreamCodec<ByteBuf, IslandBoundaryPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, IslandBoundaryPayload::active,
            ByteBufCodecs.INT, IslandBoundaryPayload::centerX,
            ByteBufCodecs.INT, IslandBoundaryPayload::centerZ,
            ByteBufCodecs.VAR_LONG, IslandBoundaryPayload::shapeSeed, IslandBoundaryPayload::new);

    public IslandBoundaryPayload {
        if (Math.abs((long) centerX) > 29_999_984 || Math.abs((long) centerZ) > 29_999_984) {
            throw new IllegalArgumentException("Island center is outside the world bounds.");
        }
    }

    @Override
    public Type<IslandBoundaryPayload> type() { return TYPE; }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, CODEC, (payload, context) ->
                IslandBoundaryState.receive(context.connection(), payload.active()
                        ? new IslandRegion(payload.centerX(), payload.centerZ(), payload.shapeSeed()) : null));
    }
}
