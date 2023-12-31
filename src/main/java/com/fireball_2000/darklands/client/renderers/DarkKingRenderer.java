package com.fireball_2000.darklands.client.renderers;

import com.bobmowzie.mowziesmobs.client.render.entity.MowzieGeoEntityRenderer;
import com.fireball_2000.darklands.DarkLands;
import com.fireball_2000.darklands.client.models.DarkKingModel;
import com.fireball_2000.darklands.server.entity.custom.DarkKingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class DarkKingRenderer extends MowzieGeoEntityRenderer<DarkKingEntity> {

	public DarkKingRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new DarkKingModel());
		this.shadowRadius = 0f;
		
	}
	
	@Override
	public ResourceLocation getTextureLocation(DarkKingEntity object) {
		// TODO Auto-generated method stub
		return new ResourceLocation(DarkLands.MOD_ID, "textures/entities/dark_king/dark_king.png");
		
	}
	
    @Override
    public RenderType getRenderType(DarkKingEntity animatable, float partialTicks, PoseStack stack,
                                    MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        stack.scale(2.4F, 2.4F, 2.4F);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }

}
