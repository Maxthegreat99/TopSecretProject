package com.fireball_2000.darklands.entity.custom;

import com.bobmowzie.mowziesmobs.server.entity.MowzieEntity;
import com.bobmowzie.mowziesmobs.server.entity.MowzieGeckoEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;


public class DarkKingEntity extends MowzieGeckoEntity implements Enemy {
	
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	
	public DarkKingEntity(EntityType<? extends MowzieEntity> type, Level world) {
		super(type, world);
	}
	
	  public static AttributeSupplier.Builder createAttributes() {
		    return MowzieEntity.createAttributes()
		        .add(Attributes.ATTACK_DAMAGE, 30.0)
		        .add(Attributes.MAX_HEALTH, 100.0)
		        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
		        .add(Attributes.MOVEMENT_SPEED, 0.25);
	  }
	
	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
	

}
