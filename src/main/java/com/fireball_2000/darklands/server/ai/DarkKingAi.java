package com.fireball_2000.darklands.server.ai;

import java.util.EnumSet;

import com.fireball_2000.darklands.server.entity.custom.DarkKingEntity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class DarkKingAi extends Goal  {
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
		return target != null && darkKing.isActive() && target.isAlive() && darkKing.areAllAttacksFalse();
	}
	
    @Override
    public void start() {
        this.repath = 0;
    }
    
    @Override
    public void stop() {
        this.darkKing.getNavigation().stop();
        darkKing.isAI = false;
    }

    @Override
    public boolean canContinueToUse() {
    	return darkKing.areAllAttacksFalse();
    }
    @Override
    public void tick() {
    	LivingEntity target = this.darkKing.getTarget();
        if (target == null || !darkKing.isActive()) return;
        double dist = this.darkKing.distanceToSqr(this.targetX, this.targetY, this.targetZ);
        this.darkKing.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if ((--this.repath <= 0 && (
            this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D ||
            target.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D) ||
            this.darkKing.getNavigation().isDone() ) && darkKing.getCurrentAnimation().equalsIgnoreCase(DarkKingEntity.ANIM_NAME_WALK)
        ) {
//    		System.out.println("AI:"+darkKing.isActive().toString());
        	darkKing.isAI = true;
            this.targetX = target.getX();
            this.targetY = target.getY();
            this.targetZ = target.getZ();
            this.repath = 4 + this.darkKing.getRandom().nextInt(7);
            if (dist > 32.0D * 32.0D) {
                this.repath += 10;
            } else if (dist > 16.0D * 16.0D) {
                this.repath += 5;
            } 
            if (!this.darkKing.getNavigation().moveTo(target, 0.5D)) {
                this.repath += 15;
            }
        }
        else if(repath <= 0 || darkKing.getNavigation().isDone() )
        	darkKing.isAI = false;
        else
        	darkKing.isAI = true;
    }

}
