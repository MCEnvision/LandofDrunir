package org.mcenvision.drunir.client;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.SmallFireball;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.mcenvision.drunir.CollectionCreatures;
import org.mcenvision.drunir.DrunirCollection;

@EventBusSubscriber(modid = DrunirCollection.ID, value = Dist.CLIENT)
public final class DrunirClient {
    private static final AnimationDefinition STATIC_POSE = AnimationDefinition.Builder.withLength(1).build();

    private DrunirClient() {}

    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (String name : DrunirCollection.CONTENT.creatures()) {
            event.registerLayerDefinition(layer(name), () -> geometry(name));
        }
        event.registerLayerDefinition(layer("magmafiend_fireball"), () -> geometry("magmafiend_fireball"));
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        CollectionCreatures.TYPES.forEach((name, type) -> event.registerEntityRenderer(type.get(), context -> new Renderer(context, name)));
        event.registerEntityRenderer(CollectionCreatures.FIREBALL.get(), FireballRenderer::new);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(DrunirCollection.location(name), "main");
    }

    static LayerDefinition geometry(String name) {
        return switch (name) {
            case "boar" -> DrunirGeometry.boar();
            case "bull" -> DrunirGeometry.bull();
            case "deer" -> DrunirGeometry.deer();
            case "drippy_goblin", "fire_goblin", "goblin_merchant", "goblin_minor" -> DrunirGeometry.goblin();
            case "lightling" -> DrunirGeometry.lightling();
            case "magmafiend" -> DrunirGeometry.magmafiend();
            case "portal_guardian" -> DrunirGeometry.portal_guardian();
            case "sandstalker" -> DrunirGeometry.sandstalker();
            case "magmafiend_fireball" -> DrunirGeometry.magmafiend_fireball();
            default -> throw new IllegalArgumentException("Unknown creature model: " + name);
        };
    }

    static AnimationDefinition walk(String name) {
        return switch (name) {
            case "boar" -> DrunirAnimations.BOAR_WALK;
            case "bull" -> DrunirAnimations.BULL_WALK;
            case "deer" -> DrunirAnimations.DEER_WALK;
            case "drippy_goblin", "fire_goblin", "goblin_merchant", "goblin_minor" -> DrunirAnimations.TROLL_WALK;
            case "magmafiend" -> DrunirAnimations.HEAVY_WALK;
            case "portal_guardian" -> DrunirAnimations.PORTALGUARDIAN_WALK;
            case "lightling", "sandstalker", "magmafiend_fireball" -> STATIC_POSE;
            default -> throw new IllegalArgumentException("Unknown creature animation: " + name);
        };
    }

    private static final class Model extends HierarchicalModel<CollectionCreatures.Creature> {
        private final ModelPart root;
        private final AnimationDefinition walk;

        private Model(ModelPart root, String name) { this.root = root; this.walk = walk(name); }

        @Override
        public ModelPart root() { return root; }

        @Override
        public void setupAnim(CollectionCreatures.Creature entity, float limbSwing, float limbSwingAmount,
                              float ageInTicks, float netHeadYaw, float headPitch) {
            root.getAllParts().forEach(ModelPart::resetPose);
            if (!walk.boneAnimations().isEmpty()) animateWalk(walk, limbSwing, limbSwingAmount, 2.0F, 2.5F);
        }
    }

    private static final class Renderer extends MobRenderer<CollectionCreatures.Creature, Model> {
        private final ResourceLocation texture;

        private Renderer(EntityRendererProvider.Context context, String name) {
            super(context, new Model(context.bakeLayer(layer(name)), name), 0.5F);
            texture = DrunirCollection.location("textures/entity/" + name + ".png");
        }

        @Override
        public ResourceLocation getTextureLocation(CollectionCreatures.Creature entity) { return texture; }
    }

    private static final class FireballRenderer extends EntityRenderer<SmallFireball> {
        private static final ResourceLocation TEXTURE = DrunirCollection.location("textures/entity/magmafiend_fireball.png");
        private final ModelPart model;

        private FireballRenderer(EntityRendererProvider.Context context) {
            super(context);
            model = context.bakeLayer(layer("magmafiend_fireball"));
        }

        @Override
        public void render(SmallFireball entity, float yaw, float partialTick, PoseStack pose,
                           MultiBufferSource buffers, int light) {
            pose.pushPose();
            pose.translate(0, 1.5, 0);
            pose.scale(-1, -1, 1);
            model.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), 15728880, OverlayTexture.NO_OVERLAY);
            pose.popPose();
            super.render(entity, yaw, partialTick, pose, buffers, light);
        }

        @Override
        public ResourceLocation getTextureLocation(SmallFireball entity) { return TEXTURE; }
    }
}
