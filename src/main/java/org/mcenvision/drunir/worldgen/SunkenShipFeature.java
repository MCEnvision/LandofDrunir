package org.mcenvision.drunir.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;

public final class SunkenShipFeature extends Feature<NoneFeatureConfiguration> {
    private static final List<String> TEMPLATES = List.of("rightsideup_full", "sideways_full",
            "upsidedown_full", "rightsideup_full_degraded");

    public SunkenShipFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placement) {
        var level = placement.level();
        var context = IslandWorldgen.context(level.getLevel());
        if (context == null) return false;
        for (var site : context.wrecks()) {
            if ((site.x() >> 4) != (placement.origin().getX() >> 4)
                    || (site.z() >> 4) != (placement.origin().getZ() >> 4)) continue;
            var template = level.getLevel().getStructureManager().getOrCreate(
                    ResourceLocation.withDefaultNamespace("shipwreck/" + TEMPLATES.get(site.variant())));
            var size = template.getSize();
            int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, site.x(), site.z());
            if (size.getY() == 0 || floor > 56) return false;
            var pivot = new BlockPos(size.getX() / 2, 0, size.getZ() / 2);
            var origin = new BlockPos(site.x() - pivot.getX(), Math.min(floor - 2, 61 - size.getY()), site.z() - pivot.getZ());
            var settings = new StructurePlaceSettings().setRotation(Rotation.values()[site.turns()])
                    .setRotationPivot(pivot).setIgnoreEntities(true).setRandom(placement.random())
                    .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
            if (!template.placeInWorld(level, origin, origin, settings, placement.random(), 2)) return false;
            for (var marker : template.filterBlocks(origin, settings, Blocks.STRUCTURE_BLOCK)) {
                if (marker.nbt() == null) continue;
                var loot = switch (marker.nbt().getString("metadata")) {
                    case "map_chest" -> BuiltInLootTables.SHIPWRECK_MAP;
                    case "treasure_chest" -> BuiltInLootTables.SHIPWRECK_TREASURE;
                    case "supply_chest" -> BuiltInLootTables.SHIPWRECK_SUPPLY;
                    default -> null;
                };
                if (loot != null) RandomizableContainer.setBlockEntityLootTable(level, placement.random(), marker.pos().below(), loot);
            }
            return true;
        }
        return false;
    }
}
