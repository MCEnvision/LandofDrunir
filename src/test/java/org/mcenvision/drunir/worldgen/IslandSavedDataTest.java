package org.mcenvision.drunir.worldgen;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandSavedDataTest {
    @Test
    void savedAnchorRoundTripsWithoutMovingOrBecomingDirty() {
        var region = new IslandRegion(-29_000_000, 29_000_000, Long.MIN_VALUE);
        var original = new IslandSavedData(region);
        assertTrue(original.isDirty());
        var restored = IslandSavedData.load(original.save(new CompoundTag(), null), null);
        assertEquals(region, restored.region());
        assertFalse(restored.isDirty());
    }

    @Test
    void missingWrongTypedAndUnsupportedAnchorsAreRejected() {
        var valid = new IslandSavedData(new IslandRegion(16, -32, 42)).save(new CompoundTag(), null);
        for (String key : new String[]{"schema", "center_x", "center_z", "shape_seed"}) {
            var missing = valid.copy();
            missing.remove(key);
            assertThrows(IllegalStateException.class, () -> IslandSavedData.load(missing, null));
        }
        var wrongType = valid.copy();
        wrongType.putString("center_x", "16");
        assertThrows(IllegalStateException.class, () -> IslandSavedData.load(wrongType, null));
        var future = valid.copy();
        future.putInt("schema", 2);
        assertThrows(IllegalStateException.class, () -> IslandSavedData.load(future, null));
        var outside = valid.copy();
        outside.putInt("center_x", Integer.MIN_VALUE);
        assertThrows(IllegalStateException.class, () -> IslandSavedData.load(outside, null));
    }
}
