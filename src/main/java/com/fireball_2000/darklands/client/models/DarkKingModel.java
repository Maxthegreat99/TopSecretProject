package com.fireball_2000.darklands.client.models;

import javax.annotation.Nullable;

import com.bobmowzie.mowziesmobs.client.model.tools.geckolib.MowzieAnimatedGeoModel;
import com.fireball_2000.darklands.DarkLands;
import com.fireball_2000.darklands.server.entity.custom.DarkKingEntity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class DarkKingModel extends MowzieAnimatedGeoModel<DarkKingEntity>{
	
	
	@Override
	public ResourceLocation getAnimationFileLocation(DarkKingEntity animatable) {
		return new ResourceLocation(DarkLands.MOD_ID, "animations/dark_king.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(DarkKingEntity object) {
		// TODO Auto-generated method stub
		return new ResourceLocation(DarkLands.MOD_ID, "geo/dark_king.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DarkKingEntity object) {
		// TODO Auto-generated method stub
		return new ResourceLocation(DarkLands.MOD_ID, "textures/entities/dark_king/dark_king.png");
	}
	

}
