package org.mcenvision.drunir.worldgen;

import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.mcenvision.drunir.DrunirCollection;

import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Set;

@EventBusSubscriber(modid = DrunirCollection.ID)
public final class IslandWorldgen {
    public static final ResourceKey<Biome> BIOME = ResourceKey.create(Registries.BIOME, DrunirCollection.location("yellow_tree_forest"));
    private static final ResourceKey<Biome> LEGACY_BIOME = ResourceKey.create(Registries.BIOME, DrunirCollection.location("golden_birch_island"));

    public static final ResourceKey<Biome> SHORE = ResourceKey.create(Registries.BIOME, DrunirCollection.location("golden_birch_shore"));

    public static final ResourceKey<Biome> WATERS = ResourceKey.create(Registries.BIOME, DrunirCollection.location("golden_birch_waters"));

    private IslandWorldgen() {}

    @SubscribeEvent
    public static void sync(OnDatapackSyncEvent event) {
        var context = context(event.getPlayerList().getServer().overworld());
        var payload = context == null ? new IslandBoundaryPayload(false, 0, 0, 0)
                : new IslandBoundaryPayload(true, context.region().centerX(), context.region().centerZ(), context.region().shapeSeed());
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }

    @SubscribeEvent
    public static void load(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) return;
        if (!(level.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator generator)
                || !(generator.getBiomeSource() instanceof MultiNoiseBiomeSource source)) return;
        var settings = generator.generatorSettings();
        if (!settings.is(NoiseGeneratorSettings.OVERWORLD) && !settings.is(NoiseGeneratorSettings.LARGE_BIOMES)
                && !settings.is(NoiseGeneratorSettings.AMPLIFIED)) return;
        IslandSavedData data = loadLocation(level);
        IslandContext context = createContext(level, source, data.region(), settings.value());
        ((IslandBiomeAccess) source).drunir$setIslandContext(context);
        ((IslandNoiseAccess) (Object) level.getChunkSource().randomState()).drunir$getIslandControl().bind(context);
    }

    @SubscribeEvent
    public static void migrateBiome(ChunkEvent.Load event) {
        if (event.isNewChunk() || !(event.getLevel() instanceof ServerLevel level)) return;
        var chunk = event.getChunk();
        var sections = chunk.getSections();
        boolean changed = false;
        for (int index = 0; index < sections.length; index++) {
            var section = sections[index];
            var previous = section.getBiomes();
            if (!previous.maybeHas(biome -> biome.is(LEGACY_BIOME))) continue;
            var replacement = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(BIOME);
            section.fillBiomesFromNoise((x, y, z, sampler) -> {
                var biome = previous.get(x & 3, y & 3, z & 3);
                return biome.is(LEGACY_BIOME) ? replacement : biome;
            }, level.getChunkSource().randomState().sampler(), chunk.getPos().x << 2,
                    chunk.getSectionYFromSectionIndex(index) << 2, chunk.getPos().z << 2);
            changed = true;
        }
        if (changed) chunk.setUnsaved(true);
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) return;
        if (level.getChunkSource().getGenerator().getBiomeSource() instanceof IslandBiomeAccess source) {
            source.drunir$setIslandContext(null);
        }
        ((IslandNoiseAccess) (Object) level.getChunkSource().randomState()).drunir$getIslandControl().bind(null);
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("drunir").then(Commands.literal("island").executes(command -> {
            ServerLevel overworld = command.getSource().getServer().overworld();
            IslandContext context = context(overworld);
            if (context == null) {
                command.getSource().sendFailure(Component.translatable("command.landofdrunir.island.unavailable"));
                return 0;
            }
            command.getSource().sendSuccess(() -> Component.translatable("command.landofdrunir.island.location",
                    context.region().centerX(), context.region().centerZ()), false);
            return 1;
        })));
    }

    public static IslandContext context(ServerLevel level) {
        return ((IslandNoiseAccess) (Object) level.getChunkSource().randomState()).drunir$getIslandControl().context();
    }

    private static IslandSavedData loadLocation(ServerLevel level) {
        boolean existingData = Files.exists(level.getServer().getWorldPath(LevelResource.ROOT)
                .resolve("data").resolve(IslandSavedData.NAME + ".dat"));
        var factory = new SavedData.Factory<>(() -> {
            // DimensionDataStorage reports failed reads as missing data. Never replace an existing anchor.
            if (existingData) {
                throw new IllegalStateException("Existing Yellow Tree Forest data could not be loaded. Preserve the data file before repairing it.");
            }
            return new IslandSavedData(IslandRegion.findUnexplored(level.getSeed(), region -> unexplored(level, region)));
        }, IslandSavedData::load);
        return level.getDataStorage().computeIfAbsent(factory, IslandSavedData.NAME);
    }

    private static IslandContext createContext(ServerLevel level, MultiNoiseBiomeSource source,
                                               IslandRegion region, NoiseGeneratorSettings settings) {
        var biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        var forest = biomes.getHolderOrThrow(BIOME);
        var shore = biomes.getHolderOrThrow(SHORE);
        var ocean = biomes.getHolderOrThrow(WATERS);
        var possible = new LinkedHashSet<>(source.possibleBiomes());
        possible.add(forest);
        possible.add(shore);
        possible.add(ocean);
        return new IslandContext(region, forest, shore, ocean, Set.copyOf(possible),
                SurfaceRules.sequence(islandSurface(), settings.surfaceRule()), IslandWreckSites.create(region));
    }

    private static SurfaceRules.RuleSource islandSurface() {
        var grass = DrunirCollection.BLOCK_ENTRIES.get("yellow_grass_block").get().defaultBlockState();
        var forest = SurfaceRules.ifTrue(SurfaceRules.isBiome(BIOME), SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.state(grass)),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.state(Blocks.DIRT.defaultBlockState()))));
        var shore = SurfaceRules.ifTrue(SurfaceRules.isBiome(SHORE, WATERS), SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.state(Blocks.SAND.defaultBlockState())),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.state(Blocks.SAND.defaultBlockState()))));
        return SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0), forest), shore));
    }

    private static boolean unexplored(ServerLevel level, IslandRegion region) {
        var directory = level.getServer().getWorldPath(LevelResource.ROOT).resolve("region");
        int margin = IslandRegion.BLEND_RADIUS + 32;
        int minX = Math.floorDiv(region.centerX() - margin, 512);
        int maxX = Math.floorDiv(region.centerX() + margin, 512);
        int minZ = Math.floorDiv(region.centerZ() - margin, 512);
        int maxZ = Math.floorDiv(region.centerZ() + margin, 512);
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (Files.exists(directory.resolve("r." + x + "." + z + ".mca"))) return false;
            }
        }
        return true;
    }
}
