package org.mcenvision.drunir.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public final class IslandSavedData extends SavedData {
    public static final String NAME = "landofdrunir_golden_island";
    private final IslandRegion region;

    public IslandSavedData(IslandRegion region) {
        this.region = region;
        setDirty();
    }

    public IslandRegion region() { return region; }

    public static IslandSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.getInt("schema") != 1 || !tag.contains("center_x", 3) || !tag.contains("center_z", 3)
                || !tag.contains("shape_seed", 4)) {
            throw new IllegalStateException("Yellow Tree Forest location data is invalid or uses an unsupported schema.");
        }
        int x = tag.getInt("center_x");
        int z = tag.getInt("center_z");
        if (Math.abs((long) x) > 29_000_000 || Math.abs((long) z) > 29_000_000) {
            throw new IllegalStateException("Yellow Tree Forest location is outside supported world coordinates.");
        }
        IslandSavedData data = new IslandSavedData(new IslandRegion(x, z, tag.getLong("shape_seed")));
        data.setDirty(false);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("schema", 1);
        tag.putInt("center_x", region.centerX());
        tag.putInt("center_z", region.centerZ());
        tag.putLong("shape_seed", region.shapeSeed());
        return tag;
    }
}
