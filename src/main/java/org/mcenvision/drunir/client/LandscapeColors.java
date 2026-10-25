package org.mcenvision.drunir.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.mcenvision.drunir.DrunirCollection;
import org.mcenvision.drunir.ShorePalette;
import org.mcenvision.drunir.ShoreSandBlock;

@EventBusSubscriber(modid = DrunirCollection.ID, value = Dist.CLIENT)
public final class LandscapeColors {
    private LandscapeColors() {}

    @SubscribeEvent
    public static void blocks(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tint) -> ShorePalette.color(state.getValue(ShoreSandBlock.TONE)),
                DrunirCollection.BLOCK_ENTRIES.get("golden_shore_sand").get());
    }
}
