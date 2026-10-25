package org.mcenvision.drunir.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.joml.Vector3f;
import org.mcenvision.drunir.DrunirCollection;
import org.mcenvision.drunir.boulder.BoulderJoin;
import org.mcenvision.drunir.boulder.BoulderShapes;
import org.mcenvision.drunir.boulder.BoulderSurface;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@EventBusSubscriber(modid = DrunirCollection.ID, value = Dist.CLIENT)
public final class ConnectedBoulderModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<BoulderJoin> JOIN = new ModelProperty<>();
    private final TextureAtlasSprite stone;
    private final Map<BoulderJoin, Mesh> cache = new LinkedHashMap<>(64, 0.75F, true);

    private record Mesh(List<BakedQuad> general, Map<Direction, List<BakedQuad>> boundary) {
        List<BakedQuad> quads(Direction side) {
            return side == null ? general : boundary.get(side);
        }
    }

    private ConnectedBoulderModel(BakedModel fallback, TextureAtlasSprite stone) {
        super(fallback);
        this.stone = stone;
    }

    @SubscribeEvent
    public static void loaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(DrunirCollection.location("connected_boulder"), new Loader());
    }

    private static final class Loader implements IGeometryLoader<Geometry> {
        @Override
        public Geometry read(JsonObject json, JsonDeserializationContext context) {
            return new Geometry(ResourceLocation.parse(json.get("fallback").getAsString()));
        }
    }

    private record Geometry(ResourceLocation fallback) implements IUnbakedGeometry<Geometry> {
        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                               Function<Material, TextureAtlasSprite> sprites, ModelState state, ItemOverrides overrides) {
            return new ConnectedBoulderModel(baker.bake(fallback, state), sprites.apply(context.getMaterial("stone")));
        }
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
        return data.derive().with(JOIN, BoulderShapes.neighborhood(level, pos, state)).build();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
        return getQuads(state, side, random, ModelData.EMPTY, null);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType type) {
        if (state == null) {
            return originalModel.getQuads(null, side, random, data, type);
        }
        var join = data.get(JOIN);
        if (join == null) {
            join = new BoulderJoin(BoulderShapes.encode(state), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        return mesh(join).quads(side);
    }

    private synchronized Mesh mesh(BoulderJoin join) {
        var existing = cache.get(join);
        if (existing != null) {
            return existing;
        }
        var result = new ArrayList<BakedQuad>();
        var boundary = new EnumMap<Direction, List<BakedQuad>>(Direction.class);
        for (var side : Direction.values()) boundary.put(side, new ArrayList<>());
        var bakery = new FaceBakery();
        for (var surface : BoulderSurface.mesh(join.geometry())) {
            var box = surface.bounds();
            float x1 = (float) box.minX(), y1 = (float) box.minY(), z1 = (float) box.minZ();
            float x2 = (float) box.maxX(), y2 = (float) box.maxY(), z2 = (float) box.maxZ();
            var face = Direction.valueOf(surface.side().name());
            float[] uv = switch (face) {
                case DOWN -> new float[]{x1, 16 - z2, x2, 16 - z1};
                case UP -> new float[]{x1, z1, x2, z2};
                case NORTH -> new float[]{16 - x2, 16 - y2, 16 - x1, 16 - y1};
                case SOUTH -> new float[]{x1, 16 - y2, x2, 16 - y1};
                case WEST -> new float[]{z1, 16 - y2, z2, 16 - y1};
                case EAST -> new float[]{16 - z2, 16 - y2, 16 - z1, 16 - y1};
            };
            var elementFace = new BlockElementFace(null, -1, "#stone", new BlockFaceUV(uv, 0));
            var quad = bakery.bakeQuad(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2),
                    elementFace, stone, face, BlockModelRotation.X0_Y0, null, true);
            (surface.boundary() ? boundary.get(face) : result).add(quad);
        }
        if (cache.size() >= 512) {
            cache.remove(cache.keySet().iterator().next());
        }
        boundary.replaceAll((side, quads) -> List.copyOf(quads));
        var mesh = new Mesh(List.copyOf(result), Map.copyOf(boundary));
        cache.put(join, mesh);
        return mesh;
    }
}
