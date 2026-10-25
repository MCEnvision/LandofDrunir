package org.mcenvision.drunir.client;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.mcenvision.drunir.DrunirCollection;
import org.mcenvision.drunir.worldgen.IslandParticles;
import org.mcenvision.drunir.worldgen.IslandBoundaryState;
import org.mcenvision.drunir.worldgen.IslandRegion;
import org.mcenvision.drunir.worldgen.IslandVeil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

@EventBusSubscriber(modid = DrunirCollection.ID, value = Dist.CLIENT)
public final class IslandAtmosphere {
    private static float mist;
    private static net.minecraft.client.multiplayer.ClientLevel previousLevel;
    private static net.minecraft.world.level.block.Block shore;
    private static net.minecraft.world.level.block.Block grass;

    private IslandAtmosphere() {}

    public static IslandRegion region() {
        var minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.level.dimension() != Level.OVERWORLD || minecraft.getConnection() == null) return null;
        return IslandBoundaryState.region(minecraft.getConnection().getConnection());
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        IslandBoundaryState.clear(event.getConnection());
        mist = 0;
        previousLevel = null;
    }

    @SubscribeEvent
    public static void debug(CustomizeGuiOverlayEvent.DebugText event) {
        var minecraft = Minecraft.getInstance();
        var camera = minecraft.getCameraEntity();
        if (camera == null || !IslandVeil.contains(region(), camera.getBlockX(), camera.getBlockZ())) return;
        var position = MysticNavigation.coordinates(minecraft.level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false));
        MysticNavigation.distort(event.getLeft(), position);
        MysticNavigation.distort(event.getRight(), position);
    }

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(IslandParticles.SHORE_MIST.get(), sprites ->
                (type, level, x, y, z, dx, dy, dz) -> new ShoreMistParticle(level, x, y, z, sprites));
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        if (previousLevel != level) {
            previousLevel = level;
            mist = 0;
        }
        if (level == null || minecraft.player == null) {
            mist = 0;
            return;
        }
        if (minecraft.isPaused()) return;
        if (shore == null) {
            shore = DrunirCollection.BLOCK_ENTRIES.get("golden_shore_sand").get();
            grass = DrunirCollection.BLOCK_ENTRIES.get("yellow_grass_block").get();
        }
        var camera = minecraft.gameRenderer.getMainCamera().getPosition();
        var pos = BlockPos.containing(camera.x, 63, camera.z);
        var region = region();
        boolean island = IslandVeil.contains(region, pos.getX(), pos.getZ());
        float target = IslandVeil.fog(region, pos.getX(), pos.getZ());
        mist += (target - mist) * 0.2F;
        if (!island || target < 0.01F || camera.y > 144 || camera.y < 50) return;
        for (int attempt = 0; attempt < 8; attempt++) {
            int x = pos.getX() + level.random.nextInt(97) - 48;
            int z = pos.getZ() + level.random.nextInt(97) - 48;
            var sample = new BlockPos(x, 63, z);
            if (!level.hasChunkAt(sample) || IslandVeil.fog(region, x, z) < 0.1F) continue;
            int height = Math.max(63, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z));
            var surface = new BlockPos(x, height - 1, z);
            var state = level.getBlockState(surface);
            if (state.getFluidState().is(FluidTags.WATER) || state.is(shore) || state.is(grass)
                    || state.is(net.minecraft.world.level.block.Blocks.SAND)) {
                level.addParticle(IslandParticles.SHORE_MIST.get(), x + 0.5, height + 0.4 + level.random.nextDouble() * 0.6,
                        z + 0.5, 0, 0, 0);
            }
        }
    }

    @SubscribeEvent
    public static void color(ViewportEvent.ComputeFogColor event) {
        if (mist < 0.01F || event.getCamera().getFluidInCamera() != FogType.NONE) return;
        event.setRed(event.getRed() + (0.66F - event.getRed()) * mist * 0.7F);
        event.setGreen(event.getGreen() + (0.75F - event.getGreen()) * mist * 0.7F);
        event.setBlue(event.getBlue() + (0.73F - event.getBlue()) * mist * 0.7F);
    }

    @SubscribeEvent
    public static void fog(ViewportEvent.RenderFog event) {
        if (mist < 0.01F || event.getType() != FogType.NONE || event.getFarPlaneDistance() <= 16) return;
        event.setNearPlaneDistance(event.getNearPlaneDistance() * (1 - mist) + 10 * mist);
        event.setFarPlaneDistance(event.getFarPlaneDistance() * (1 - mist)
                + Math.min(event.getFarPlaneDistance(), 76) * mist);
        event.setFogShape(FogShape.CYLINDER);
        event.setCanceled(true);
    }
}
