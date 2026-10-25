package org.mcenvision.drunir.boulder;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import org.mcenvision.drunir.DrunirCollection;

import java.util.ArrayList;
import java.util.List;

public final class CollectionBoulders {
    public static final List<DeferredItem<BoulderPieceItem>> PIECES = new ArrayList<>();

    private CollectionBoulders() {}

    public static void registerItems() {
        var catalog = BoulderCatalog.INSTANCE;
        for (var material : catalog.materials()) {
            var block = DrunirCollection.BLOCK_ENTRIES.get(BoulderCatalog.blockId(material.id()));
            for (var size : catalog.sizes()) {
                for (int index = 0; index < catalog.forms().size(); index++) {
                    int form = index;
                    String id = BoulderCatalog.itemId(material.id(), size.id(), catalog.forms().get(index).id());
                    PIECES.add(DrunirCollection.ITEMS.register(id, () -> new BoulderPieceItem(
                            block.get(), size.layers(), form, false, new Item.Properties())));
                }
            }
        }
    }
}
