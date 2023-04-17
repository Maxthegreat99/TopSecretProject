package com.fireball_2000.darklands.ai;

import java.util.EnumSet;

import com.fireball_2000.darklands.entity.custom.DarkKingEntity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class DarkKingAi extends Goal {
	private final DarkKingEntity darkKing;
    private int repath;
    private double targetX;
    private double targetY;
    private double targetZ;
    
	public DarkKingAi(DarkKingEntity darkKing) {
		this.darkKing = darkKing;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
	}
	@Override
	public boolean canUse() {
		LivingEntity target = darkKing.getTarget();
		return target != null && target.isAlive();
	}
	
    @Override
    public void start() {
        this.repath = 0;
    }
    
    @Override
    public void stop() {
        this.darkKing.getNavigation().stop();
    }
    
    @Override
    public void tick() {
    	LivingEntity target = this.darkKing.getTarget();
    }

}
