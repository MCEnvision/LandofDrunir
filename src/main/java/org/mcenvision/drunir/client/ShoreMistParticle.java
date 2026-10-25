package org.mcenvision.drunir.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

final class ShoreMistParticle extends TextureSheetParticle {
    ShoreMistParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        pickSprite(sprites);
        lifetime = 180 + random.nextInt(80);
        quadSize = 3.5F + random.nextFloat() * 3;
        xd = 0.008 + random.nextDouble() * 0.006;
        yd = 0;
        zd = -0.004;
        friction = 1;
        hasPhysics = false;
        alpha = 0;
        setColor(0.78F, 0.85F, 0.82F);
    }

    @Override
    public void tick() {
        super.tick();
        float fade = Math.min(Math.min(age / 35F, (lifetime - age) / 65F), 1);
        alpha = Math.max(0, fade) * 0.11F;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        var eye = camera.getPosition();
        float px = (float) (Mth.lerp(partialTick, xo, x) - eye.x);
        float py = (float) (Mth.lerp(partialTick, yo, y) - eye.y);
        float pz = (float) (Mth.lerp(partialTick, zo, z) - eye.z);
        for (int vertex = 0; vertex < 4; vertex++) {
            boolean right = vertex >= 2;
            boolean top = vertex == 1 || vertex == 2;
            var point = new Vector3f(right ? quadSize : -quadSize, (top ? 1 : -1) * quadSize * 0.22F, 0)
                    .rotate(camera.rotation()).add(px, py, pz);
            buffer.addVertex(point.x, point.y, point.z).setUv(right ? getU0() : getU1(), top ? getV0() : getV1())
                    .setColor(rCol, gCol, bCol, alpha).setLight(15728880);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
