package org.mcenvision.drunir.client;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public final class DrunirGeometry {
    private DrunirGeometry() {}

    public static LayerDefinition boar() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-5.0F, -10.0F, -8.0F, 10.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

        body.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 7.0F, -7.0F, -1.5708F, 0.0F, 0.0F));

        body.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 7.0F, -7.0F, -1.5708F, 0.0F, 0.0F));

        body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -7.0F, -7.0F, -1.5708F, 0.0F, 0.0F));

        body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -7.0F, -7.0F, -1.5708F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, -6.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static LayerDefinition bull() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bull = partdefinition.addOrReplaceChild("bull", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 1.0F));

        PartDefinition head = bull.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -19.0F, -10.0F));

        head.addOrReplaceChild("nose_r1", CubeListBuilder.create().texOffs(55, 59).addBox(-2.0F, 0.7588F, -4.75F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(45, 0).addBox(-4.0F, -4.2412F, -2.75F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.2412F, -6.25F, 0.2182F, 0.0F, 0.0F));

        PartDefinition horns = head.addOrReplaceChild("horns", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 6.0F));

        PartDefinition hornleft = horns.addOrReplaceChild("hornleft", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, -22.0F, -11.5F, -0.2618F, 0.0F, 0.0F));

        hornleft.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(60, 49).addBox(5.0F, -2.3599F, -4.812F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(12, 0).addBox(4.0F, -2.3599F, -0.812F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 2.3589F, -0.1444F, 0.2182F, 0.0F, 0.0F));

        PartDefinition hornright = horns.addOrReplaceChild("hornright", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -22.0F, -11.5F, -0.2618F, 0.0F, 0.0F));

        hornright.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 64).addBox(-7.0F, -2.3599F, -4.812F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(14, 14).addBox(-5.0F, -2.3599F, -0.812F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 2.3589F, -0.1444F, 0.2182F, 0.0F, 0.0F));

        PartDefinition body = bull.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -24.0F, -10.0F, 12.0F, 12.0F, 21.0F, new CubeDeformation(0.0F))
                .texOffs(0, 33).addBox(-7.0F, -25.0F, -11.0F, 14.0F, 15.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, -1.0F));

        body.addOrReplaceChild("backll", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -10.0F, 7.0F));

        body.addOrReplaceChild("frontll", CubeListBuilder.create().texOffs(44, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -9.0F, -9.0F));

        body.addOrReplaceChild("backrl", CubeListBuilder.create().texOffs(0, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -9.0F, 7.0F));

        body.addOrReplaceChild("frontrl", CubeListBuilder.create().texOffs(60, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -9.0F, -9.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static LayerDefinition deer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition deer = partdefinition.addOrReplaceChild("deer", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, -1.0F));

        PartDefinition upper = deer.addOrReplaceChild("upper", CubeListBuilder.create(), PartPose.offset(0.0F, -17.0F, -6.0F));

        PartDefinition head2 = upper.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(1, 31).addBox(-2.5F, -10.5F, -6.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, -2.0F));

        head2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(3, 15).addBox(-2.0F, -23.0F, -17.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.5F, 9.0F));

        PartDefinition RE = head2.addOrReplaceChild("RE", CubeListBuilder.create(), PartPose.offset(-2.0F, -9.5F, 0.25F));

        RE.addOrReplaceChild("earright_r1", CubeListBuilder.create().texOffs(6, 9).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition RA = head2.addOrReplaceChild("RA", CubeListBuilder.create(), PartPose.offset(0.0F, 15.5F, 9.0F));

        RA.addOrReplaceChild("antlersright_r1", CubeListBuilder.create().texOffs(44, 33).addBox(-1.5F, -8.0F, 0.0F, 7.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -26.0F, -11.0F, -0.0873F, -0.9163F, -0.6545F));

        PartDefinition LA = head2.addOrReplaceChild("LA", CubeListBuilder.create(), PartPose.offset(0.0F, 15.5F, 9.0F));

        LA.addOrReplaceChild("antlersleft_r1", CubeListBuilder.create().texOffs(29, 33).addBox(-5.5F, -8.0F, 0.0F, 7.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -26.0F, -11.0F, -0.0873F, 0.9163F, 0.6545F));

        PartDefinition LE = head2.addOrReplaceChild("LE", CubeListBuilder.create(), PartPose.offset(0.0F, 15.5F, 9.0F));

        LE.addOrReplaceChild("earleft_r1", CubeListBuilder.create().texOffs(6, 9).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -26.0F, -8.75F, 0.0F, 0.0F, 0.7854F));

        head2.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(49, 9).addBox(-2.0F, -21.0F, -10.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.5F, 9.0F));

        deer.addOrReplaceChild("body", CubeListBuilder.create().texOffs(1, 4).addBox(-4.0F, -19.0F, -8.0F, 8.0F, 8.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(36, 12).addBox(-1.5F, -20.0F, 9.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        deer.addOrReplaceChild("BRL", CubeListBuilder.create().texOffs(27, 45).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -11.0F, 8.0F));

        deer.addOrReplaceChild("BLL", CubeListBuilder.create().texOffs(40, 45).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -11.0F, 8.0F));

        deer.addOrReplaceChild("FRL", CubeListBuilder.create().texOffs(1, 45).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -11.0F, -6.0F));

        deer.addOrReplaceChild("FLL", CubeListBuilder.create().texOffs(14, 45).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -11.0F, -6.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition goblin() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition troll = partdefinition.addOrReplaceChild("troll", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = troll.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, -10.0F, -1.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

        body.addOrReplaceChild("rarm", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, -1.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -7.5F, 0.0F));

        body.addOrReplaceChild("larm", CubeListBuilder.create().texOffs(12, 11).addBox(-2.0F, -1.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -7.5F, 0.0F));

        body.addOrReplaceChild("lleg", CubeListBuilder.create().texOffs(8, 20).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -4.5F, 0.0F));

        body.addOrReplaceChild("rleg", CubeListBuilder.create().texOffs(18, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -5.0F, 0.0F));

        troll.addOrReplaceChild("head", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -2.0F, -5.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -5.0F, -3.0F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(24, 5).addBox(3.0F, -5.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(20, 11).addBox(-7.0F, -5.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -9.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static LayerDefinition lightling() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition branch = partdefinition.addOrReplaceChild("branch", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.0F, 0.0F));

		branch.addOrReplaceChild("lamp1", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -6.0F, 0.0F));

		branch.addOrReplaceChild("lamp2", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, -1.0F, -1.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -9.0F, 0.0F));

		branch.addOrReplaceChild("lamp3", CubeListBuilder.create().texOffs(0, 29).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -15.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(12, 29).addBox(0.0F, 0.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 22.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(12, 29).mirror().addBox(-3.0F, 0.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.0F, 22.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

    public static LayerDefinition magmafiend() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition magma_golem = partdefinition.addOrReplaceChild("magma_golem", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = magma_golem.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 57).addBox(-9.5F, -21.5F, -9.0F, 19.0F, 22.0F, 17.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -20.5F, 0.0F));

        PartDefinition arm_l = body.addOrReplaceChild("arm_l", CubeListBuilder.create(), PartPose.offset(-8.4F, -18.1F, 0.0F));

        arm_l.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(62, 23).mirror().addBox(-9.5F, -7.5F, -6.0F, 13.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.4F, 2.1F, 0.0F, 0.0F, 0.0F, -0.6545F));

        PartDefinition arm_l_joint = arm_l.addOrReplaceChild("arm_l_joint", CubeListBuilder.create(), PartPose.offset(-7.6F, 6.1F, 0.0F));

        arm_l_joint.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(82, 0).mirror().addBox(-0.0142F, -0.0246F, -4.0F, 7.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.4F, 2.9F, 0.0F, 0.0F, 0.0F, -1.0036F));

        PartDefinition unterarm_l = arm_l_joint.addOrReplaceChild("unterarm_l", CubeListBuilder.create(), PartPose.offset(-3.0F, 6.0F, 0.0F));

        unterarm_l.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 10).mirror().addBox(-13.0142F, -1.0246F, -6.0F, 13.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.4F, -3.1F, 0.0F, 0.0F, 0.0F, -1.309F));

        PartDefinition finger_l2 = unterarm_l.addOrReplaceChild("finger_l2", CubeListBuilder.create(), PartPose.offset(-5.9F, 9.9F, 3.5F));

        finger_l2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(74, 87).mirror().addBox(-19.0142F, -1.0246F, 1.0F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, -13.0F, -3.5F, 0.0F, 0.0F, -1.309F));

        PartDefinition thump = unterarm_l.addOrReplaceChild("thump", CubeListBuilder.create(), PartPose.offset(-0.4F, 7.9F, -6.0F));

        thump.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-18.0142F, 3.9754F, -8.0F, 10.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -11.0F, 6.0F, 0.0F, 0.0F, -1.309F));

        PartDefinition finger_l = unterarm_l.addOrReplaceChild("finger_l", CubeListBuilder.create(), PartPose.offset(-5.9F, 9.9F, -4.0F));

        finger_l.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(74, 87).mirror().addBox(-19.0142F, -1.0246F, -6.0F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, -13.0F, 4.0F, 0.0F, 0.0F, -1.309F));

        PartDefinition arm_r = body.addOrReplaceChild("arm_r", CubeListBuilder.create(), PartPose.offset(8.4F, -18.1F, 0.0F));

        arm_r.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(62, 23).addBox(-3.5F, -7.5F, -6.0F, 13.0F, 11.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, 2.1F, 0.0F, 0.0F, 0.0F, 0.6545F));

        PartDefinition arm_r_joint = arm_r.addOrReplaceChild("arm_r_joint", CubeListBuilder.create(), PartPose.offset(8.6F, 5.1F, 0.0F));

        arm_r_joint.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(82, 0).addBox(-6.9858F, -0.0246F, -4.0F, 7.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.4F, 3.9F, 0.0F, 0.0F, 0.0F, 1.0036F));

        PartDefinition unterarm_r = arm_r_joint.addOrReplaceChild("unterarm_r", CubeListBuilder.create(), PartPose.offset(2.4F, 6.9F, 0.0F));

        unterarm_r.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 10).addBox(0.0142F, -1.0246F, -6.0F, 13.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -3.0F, 0.0F, 0.0F, 0.0F, 1.309F));

        PartDefinition finger_r = unterarm_r.addOrReplaceChild("finger_r", CubeListBuilder.create(), PartPose.offset(5.5F, 10.0F, 4.0F));

        finger_r.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(74, 87).addBox(13.0142F, -1.0246F, 1.0F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -13.0F, -4.0F, 0.0F, 0.0F, 1.309F));

        PartDefinition thump_r = unterarm_r.addOrReplaceChild("thump_r", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, -6.0F));

        thump_r.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 0).addBox(8.0142F, 3.9754F, -8.0F, 10.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -11.0F, 6.0F, 0.0F, 0.0F, 1.309F));

        PartDefinition finger_r2 = unterarm_r.addOrReplaceChild("finger_r2", CubeListBuilder.create(), PartPose.offset(5.4F, 10.0F, -3.5F));

        finger_r2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(74, 87).addBox(13.0142F, -1.0246F, -6.0F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.4F, -13.0F, 3.5F, 0.0F, 0.0F, 1.309F));

        body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(40, 3).addBox(-4.5F, -6.0F, -6.0F, 9.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -20.5F, 0.0F));

        PartDefinition leg_r = magma_golem.addOrReplaceChild("leg_r", CubeListBuilder.create(), PartPose.offset(5.0F, -24.0F, 0.5F));

        PartDefinition bone = leg_r.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.5F));

        bone.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 33).addBox(-5.2632F, -1.7694F, -6.5F, 11.0F, 10.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 33).addBox(-5.2632F, -1.7694F, -6.5F, 11.0F, 10.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        PartDefinition leg_r_joint = leg_r.addOrReplaceChild("leg_r_joint", CubeListBuilder.create(), PartPose.offset(3.0F, 8.0F, -0.5F));

        leg_r_joint.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(78, 71).addBox(-8.0F, -6.0F, -4.5F, 8.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 4.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        leg_r_joint.addOrReplaceChild("unterleg_r", CubeListBuilder.create().texOffs(56, 47).addBox(-5.0F, -1.0F, -5.5F, 10.0F, 12.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition leg_l = magma_golem.addOrReplaceChild("leg_l", CubeListBuilder.create(), PartPose.offset(-5.0F, -24.0F, 0.5F));

        PartDefinition WL = leg_l.addOrReplaceChild("WL", CubeListBuilder.create(), PartPose.offset(-7.0F, 12.0F, -0.5F));

        WL.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(0, 33).mirror().addBox(-1.5F, -15.0F, -6.5F, 11.0F, 10.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 33).mirror().addBox(-1.5F, -15.0F, -6.5F, 11.0F, 10.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

        PartDefinition leg_l_joint = leg_l.addOrReplaceChild("leg_l_joint", CubeListBuilder.create(), PartPose.offset(-2.0F, 7.0F, -0.5F));

        leg_l_joint.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(78, 71).mirror().addBox(0.0F, -6.0F, -4.5F, 8.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, 5.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

        leg_l_joint.addOrReplaceChild("unterleg_l", CubeListBuilder.create().texOffs(56, 47).mirror().addBox(-6.0F, -1.0F, -5.5F, 10.0F, 12.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static LayerDefinition portal_guardian() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create(), PartPose.offset(0.0F, 29.0F, 0.0F));

		PartDefinition Body = Root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(40, 23).addBox(-7.0F, -1.0F, -3.0F, 14.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -21.0F, 0.0F));

		PartDefinition RightArm = Body.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 18).addBox(0.0F, -2.0F, -5.0F, 8.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(58, 70).addBox(1.0F, 4.0F, -4.0F, 6.0F, 7.5F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(9.0F, -18.0F, 0.0F));

		PartDefinition RightBottomElbow = RightArm.addOrReplaceChild("RightBottomElbow", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 11.0F, 0.0F, 0.3491F, 0.0F, 0.0F));

		RightBottomElbow.addOrReplaceChild("RightBottomElbow_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-3.0F, -6.0F, -6.5F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition RightHand = RightBottomElbow.addOrReplaceChild("RightHand", CubeListBuilder.create(), PartPose.offset(0.0F, 7.0F, -3.0F));

		RightHand.addOrReplaceChild("RightHand_r1", CubeListBuilder.create().texOffs(50, 38).addBox(-4.0F, 0.0F, -0.5F, 8.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.9051F, -3.8096F, -0.3491F, 0.0F, 0.0F));

		PartDefinition LeftArm = Body.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 18).addBox(-8.0F, -3.0F, -5.0F, 8.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
				.texOffs(58, 70).addBox(-7.0F, 3.5F, -4.0F, 6.0F, 7.25F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -17.0F, 0.0F));

		PartDefinition LeftArmBottomElbow = LeftArm.addOrReplaceChild("LeftArmBottomElbow", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 11.0F, 0.5F, 0.3491F, 0.0F, 0.0F));

		LeftArmBottomElbow.addOrReplaceChild("LeftArmBottomElbow_r1", CubeListBuilder.create().texOffs(0, 64).addBox(-3.0F, -6.0F, -6.5F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition LeftHand = LeftArmBottomElbow.addOrReplaceChild("LeftHand", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, -2.0F));

		LeftHand.addOrReplaceChild("LeftHand_r1", CubeListBuilder.create().texOffs(50, 38).addBox(-4.0F, 0.0F, -0.5F, 8.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.9051F, -4.8096F, -0.3491F, 0.0F, 0.0F));

		PartDefinition Club = LeftHand.addOrReplaceChild("Club", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 0.0F));

		Club.addOrReplaceChild("Club_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 2.0F, 2.0F, 4.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -13.0F, -23.0F, -0.3491F, 0.0F, 0.0F));

		Club.addOrReplaceChild("Club_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -20.0F, 2.0F, 2.0F, 36.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -5.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition Upper = Body.addOrReplaceChild("Upper", CubeListBuilder.create().texOffs(0, 38).addBox(-8.5F, -11.0F, -4.0F, 17.0F, 18.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -8.0F, 0.0F));

		PartDefinition Head = Upper.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, -11.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

		Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 0).addBox(20.5F, -7.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -16.5F, -1.0F, 0.0F, 0.0F, 0.1745F));

		Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(0, 18).addBox(-1.75F, -3.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -16.5F, -1.0F, 0.0F, 0.0F, -0.1745F));

		Head.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(42, 38).addBox(12.25F, 1.4F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -12.0F, -1.0F, 0.0F, 0.0F, -0.1745F));

		Head.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(42, 42).addBox(-5.25F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -12.0F, -1.0F, 0.0F, 0.0F, 0.1745F));

		Head.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(40, 0).addBox(-5.0F, -6.5F, -5.5F, 10.0F, 13.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.5F, -0.5F, 0.0F, 0.7854F, 0.0F));

		PartDefinition RightLeg = Root.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(28, 64).addBox(-3.0F, 0.0F, -4.0F, 7.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -19.75F, 0.0F));

		RightLeg.addOrReplaceChild("RightShin", CubeListBuilder.create().texOffs(50, 52).addBox(-3.0F, -2.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.0F, 0.0F));

		PartDefinition LeftLeg = Root.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(28, 64).addBox(-3.0F, -1.0F, -4.0F, 7.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -18.75F, 0.0F));

		LeftLeg.addOrReplaceChild("LeftShin", CubeListBuilder.create().texOffs(50, 52).addBox(-4.0F, 0.0F, -4.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 4.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 96, 96);
	}

    public static LayerDefinition sandstalker() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition torso = partdefinition.addOrReplaceChild("torso", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 22.0F, -3.0F, -0.0873F, 0.0F, 0.0F));

		torso.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -3.0F, 8.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 3.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition leg_l1 = torso.addOrReplaceChild("leg_l1", CubeListBuilder.create(), PartPose.offset(2.0F, 1.0F, 0.0F));

		leg_l1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(27, 0).addBox(0.0F, -1.0F, -5.0F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition leg_r2 = torso.addOrReplaceChild("leg_r2", CubeListBuilder.create(), PartPose.offset(-2.0F, 1.0F, 1.6F));

		leg_r2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 0).mirror().addBox(-5.0F, -1.0F, -1.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, -0.1F, 0.0873F, 0.0F, 0.0F));

		PartDefinition leg_l2 = torso.addOrReplaceChild("leg_l2", CubeListBuilder.create(), PartPose.offset(2.0F, 1.0F, 1.7F));

		leg_l2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(48, 0).addBox(0.0F, -1.0F, -1.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -0.2F, 0.0873F, 0.0F, 0.0F));

		PartDefinition leg_r3 = torso.addOrReplaceChild("leg_r3", CubeListBuilder.create(), PartPose.offset(-2.0F, 1.0F, 4.0F));

		leg_r3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(48, 0).mirror().addBox(-5.0F, -1.0F, -1.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, 1.5F, 0.0873F, 0.0F, 0.0F));

		PartDefinition leg_l3 = torso.addOrReplaceChild("leg_l3", CubeListBuilder.create(), PartPose.offset(2.0F, 1.0F, 4.0F));

		leg_l3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(48, 0).addBox(0.0F, -1.0F, -1.5F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 1.5F, 0.0873F, 0.0F, 0.0F));

		PartDefinition leg_r1 = torso.addOrReplaceChild("leg_r1", CubeListBuilder.create(), PartPose.offset(-2.0F, 1.0F, 0.0F));

		leg_r1.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(27, 0).mirror().addBox(-5.0F, -1.0F, -5.0F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		head.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(22, 17).addBox(-3.0F, -3.0F, -8.0F, 6.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 3.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition glaw_l = head.addOrReplaceChild("glaw_l", CubeListBuilder.create(), PartPose.offset(3.0F, 0.0F, -4.0F));

		glaw_l.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(7, 17).addBox(-2.0F, 0.0F, -3.5F, 4.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0.2179F, -2.4905F, 0.0873F, 0.0F, 0.0F));

		PartDefinition glaw_r = head.addOrReplaceChild("glaw_r", CubeListBuilder.create(), PartPose.offset(-3.0F, 0.0F, -4.0F));

		glaw_r.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(-1, 17).addBox(-1.0F, 0.0F, -6.0F, 4.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

    public static LayerDefinition magmafiend_fireball() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -14.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        return LayerDefinition.create(meshdefinition, 32, 16);
    }
}
