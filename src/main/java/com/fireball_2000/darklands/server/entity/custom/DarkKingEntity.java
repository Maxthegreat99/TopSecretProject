package com.fireball_2000.darklands.server.entity.custom;

import java.util.ArrayList;
import java.util.List;

import com.bobmowzie.mowziesmobs.server.ai.MMPathNavigateGround;
import com.bobmowzie.mowziesmobs.server.entity.MowzieEntity;
import com.bobmowzie.mowziesmobs.server.entity.MowzieGeckoEntity;
import com.bobmowzie.mowziesmobs.server.entity.SmartBodyHelper;
import com.fireball_2000.darklands.server.ai.DarkKingAi;
import com.fireball_2000.darklands.server.packets.EdataParsed;
import com.fireball_2000.darklands.server.packets.PacketHandler;
import com.fireball_2000.darklands.server.packets.sendDataToDarkKing;
import com.mojang.math.Vector3d;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;


//TO GET DONE: main movement ai
//			 : main switch attack ai
//			 : get the targetting and attacking working
//			 : get the animations to work
//           : rework predicate entirely
//           : work on custom animation tracking
//           : change the way current animation works



public class DarkKingEntity extends MowzieGeckoEntity implements Enemy  {
	
	// geckolib
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	
	
	private static final EntityDataAccessor<String> NEWANIMATION = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.STRING);
	
	private static final EntityDataAccessor<String> CURRENTANIMATION = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.STRING);
	
	private static final EntityDataAccessor<Integer> ATTACKTRANS = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.INT);
	
	private static final EntityDataAccessor<Boolean> ACTIVE = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.BOOLEAN);
	
	private static final EntityDataAccessor<Boolean> ISANIMATIONNEAREND = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.BOOLEAN);
	
	private static final EntityDataAccessor<Boolean> ACTIVATING = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.BOOLEAN);
	
	private static final EntityDataAccessor<Integer> CURRENTANIMTICK = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.INT);
	
	private static final EntityDataAccessor<Boolean> STARTANIM = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.BOOLEAN);
	
	private static final EntityDataAccessor<Boolean> HASTARGET = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.BOOLEAN);
	
	private static final EntityDataAccessor<Float> TARGETDISTANCE = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.FLOAT);
	
	public static final double ANIM_GROUNDATT_TIME = 4.4583d;
	public static final double ANIM_DOWNTHRUST_TIME = 3.58d;
	public static final double ANIM_SLASH2_TIME = 1.33333d;
	public static final double ANIM_SLASH1_TIME = 1.58333d;
	public static final double ANIM_RUN_TIME = 0.6667d;
	public static final double ANIM_START_TIME = 6.2917d;
	public static final double ANIM_JUMPPREP_TIME = 2.0417d;
	public static final double ANIM_JUMPEND_TIME = 0.9583d;
	
	public final double LOOKAT_LAPSE_SLASH1 = 0.87d;
	public final double LOOKAT_LAPSE_SLASH2 = 0.54d;
	public final double LOOKAT_LAPSE_DOWNTHRUST = 1.25d;
	public final double LOOKAT_LAPSE_GROUNDATT = 1.33d;
	
	public final double ATTACK_RANGE_SLASH = 5f;
	public final double ATTACK_RANGE_DOWNTHURST = 6.5f;
	
	public final double ATTACK_ARC_DOWNTHURST = 30f;
	public final double ATTACK_ARC_SLASH = 232.5f;
	
	
	public final int ATTACK_TICK_SLASH1 = this.secsToTick(1.25d);
	public final int ATTACK_TICK_SLASH2 = this.secsToTick(0.90d);
	public final int ATTACK_TICK_DOWNTHRUST = this.secsToTick(1.60d);	
	public final int ATTACK_TICK_GROUNDATT = this.secsToTick(1.72d);
	public final int ATTACK_TICK_JUMPPREP = this.secsToTick(1.08d);
	
	public final int ANIM_GROUNDATT_TICK = this.secsToTick(ANIM_GROUNDATT_TIME);
	public final int ANIM_DOWNTHRUST_TICK = this.secsToTick(ANIM_DOWNTHRUST_TIME);
	public final int ANIM_SLASH2_TICK = this.secsToTick(ANIM_SLASH2_TIME);
	public final int ANIM_SLASH1_TICK = this.secsToTick(ANIM_SLASH1_TIME);
	public final int ANIM_RUN_TICK = this.secsToTick(ANIM_RUN_TIME);
	public final int ANIM_START_TICK = this.secsToTick(ANIM_START_TIME);
	public final int ANIM_JUMPPREP_TICK = this.secsToTick(ANIM_JUMPPREP_TIME);
	public final int ANIM_JUMPEND_TICK = this.secsToTick(ANIM_JUMPEND_TIME);
	
	private static final EntityDataAccessor<Boolean> ATTACK_NORMAL = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ATTACK_LEAP = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ATTACK_GROUND = SynchedEntityData.defineId(DarkKingEntity.class,EntityDataSerializers.BOOLEAN);

	
	private static final List<EntityDataAccessor<Boolean>> ALL_ATTACKS = new ArrayList<EntityDataAccessor<Boolean>>();
	
	public static final String ANIM_NAME_PREFIX = "animation.darkKingSprite.";
	public static final String ANIM_NAME_IDLE = ANIM_NAME_PREFIX + "startpose";
	public static final String ANIM_NAME_WALK = ANIM_NAME_PREFIX + "run";
	public static final String ANIM_NAME_START = ANIM_NAME_PREFIX + "start";
	public static final String ANIM_NAME_SLASH1 = ANIM_NAME_PREFIX + "slash1";
	public static final String ANIM_NAME_SLASH2 = ANIM_NAME_PREFIX + "slahs2";
	public static final String ANIM_NAME_DOWNTHURST = ANIM_NAME_PREFIX + "DownThurst";
	public static final String ANIM_NAME_GROUNDATTACK = ANIM_NAME_PREFIX + "GroundAttac";
	public static final String ANIM_NAME_JUMPPREP = ANIM_NAME_PREFIX + "JumpPrep";
	public static final String ANIM_NAME_JUMPEND = ANIM_NAME_PREFIX + "JumpEnd";
	public static final String ANIM_NAME_JUMP = ANIM_NAME_PREFIX + "Jump";
	
	private double prevZ = 0D;
	private double prevX = 0D;
	private int a = 0;
	
	public Boolean needCurrentAnim = false;
	public Boolean needCurrentTick = false;
	public Boolean needActivation = false;
	public Boolean needDeavation = false;
	public Boolean needAnimStart = false;
	
	public String currentAnim;
	public int currentTick = 0;
	
	public Boolean isAttack = true;
	
	public Boolean didPart1 = false;
	public Boolean didPart2 = false;
	public Boolean didPart3 = false;
	
	public Boolean didTransitionProperly = false;
	
	public Boolean waitingForRegister = false;
	
	private EntityDataAccessor<Boolean> ATTACK_TOREG;
	
	public static final double AIR_RESISTANCE = 0.98d;
	
	public static final double GRAVITY = 0.08d;
	
	public Boolean isAI = false;

	
	public DarkKingEntity(EntityType<? extends MowzieEntity> type, Level world) {
		super(type, world);	
	
		ALL_ATTACKS.add(ATTACK_NORMAL);
		ALL_ATTACKS.add(ATTACK_LEAP);
		ALL_ATTACKS.add(ATTACK_GROUND);
	}
	
	public static AttributeSupplier.Builder createAttributes() {
		return MowzieEntity.createAttributes()
		        .add(Attributes.ATTACK_DAMAGE, 30.0)
		        .add(Attributes.MAX_HEALTH, 100.0)
		        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
		        .add(Attributes.ARMOR, 5.0)
		        .add(Attributes.FOLLOW_RANGE, 30.0);
		    

	}
	@Override
	protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new DarkKingAi(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, null));
	} 
	  
    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        getEntityData().define(ACTIVATING, false);
        getEntityData().define(ACTIVE, false);
        getEntityData().define(ISANIMATIONNEAREND, false);
        getEntityData().define(CURRENTANIMTICK, 0);
        getEntityData().define(ATTACKTRANS, 0 );
        getEntityData().define(CURRENTANIMATION, "");
        getEntityData().define(NEWANIMATION, "");
        getEntityData().define(ATTACK_NORMAL, false);
        getEntityData().define(ATTACK_LEAP, false);
        getEntityData().define(ATTACK_GROUND, false);
        getEntityData().define(STARTANIM, false);   
        getEntityData().define(HASTARGET,false);   
        getEntityData().define(TARGETDISTANCE,0.0f);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("active", isActive());
        compound.putBoolean("activating", isActivating());
        compound.putBoolean("isAnimationNearEnd",isAnimNearEnd());
        compound.putInt("currentTick", getCurrentTick());
        compound.putInt("attackTrans", getAttackTrans());
        compound.putString("currentAnimation", getCurrentAnimation());
        compound.putString("newAnimation", getNewAnimation());
        compound.putBoolean("attackNormal", isAttackNormal());
        compound.putBoolean("attackLeap", isAttackLeap());
        compound.putBoolean("attackGround", isAttackGround());
        compound.putBoolean("startAnim", isStartAnim());
        compound.putBoolean("hasTarget", isHasTarget());
        compound.putFloat("targetDistance",getTargetDistance());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setActive(compound.getBoolean("active"));
        setActivating(compound.getBoolean("activating"));
        setAnimNearEnd(compound.getBoolean("isAnimationNearEnd"));
        setCurrentTick(compound.getInt("currentTick"));
        setAttackTrans(compound.getInt("attackTrans"));
        setCurrentAnimation(compound.getString("currentAnimation"));
        setNewAnimation(compound.getString("newAnimation"));
        setAttackNormal(compound.getBoolean("attackNormal"));
        setAttackLeap(compound.getBoolean("attackLeap"));
        setAttackGround(compound.getBoolean("attackGround"));
        setStartAnim(compound.getBoolean("startAnim"));
        setHasTarget(compound.getBoolean("hasTarget"));
        setTargetDistance(compound.getFloat("targetDisatance"));
    }
    public float getTargetDistance() {
    	return getEntityData().get(TARGETDISTANCE);
    }
    public void setTargetDistance(float dist) {
    	getEntityData().set(TARGETDISTANCE, dist);
    }
    public Boolean isHasTarget() {
    	return getEntityData().get(HASTARGET);
    }
    public void setHasTarget(Boolean has) {
    	getEntityData().set(HASTARGET, has);
    }
    public Boolean isStartAnim() {
    	return getEntityData().get(STARTANIM);
    }
    public void setStartAnim(Boolean a) {
    	getEntityData().set(STARTANIM,a);
    }
    public Boolean isAttackNormal() {
    	return getEntityData().get(ATTACK_NORMAL);
    }
    public void setAttackNormal(Boolean isAttack) {
    	getEntityData().set(ATTACK_NORMAL,isAttack);
    }
    public Boolean isAttackLeap() {
    	return getEntityData().get(ATTACK_LEAP);
    }
    public void setAttackLeap(Boolean isAttack) {
    	getEntityData().set(ATTACK_LEAP,isAttack);
    }
    public Boolean isAttackGround() {
    	return getEntityData().get(ATTACK_GROUND);
    }
    public void setAttackGround(Boolean isAttack) {
    	getEntityData().set(ATTACK_GROUND,isAttack);
    }
    public Boolean isActivating() {
    	return getEntityData().get(ACTIVATING);
    }
    
    public void setActivating(boolean isActivating) {
        getEntityData().set(ACTIVATING, isActivating);
    }
    public Boolean isAnimNearEnd() {
    	return getEntityData().get(ISANIMATIONNEAREND);
    }
    
    public void setAnimNearEnd(boolean isAnimNearEnd) {
        getEntityData().set(ACTIVE, isAnimNearEnd);
    }
    public int getCurrentTick() {
    	return getEntityData().get(CURRENTANIMTICK);
    }
    
    public void setCurrentTick(int tick) {
        getEntityData().set(CURRENTANIMTICK, tick);
    }
    public int getAttackTrans() {
    	return getEntityData().get(ATTACKTRANS);
    }
    
    public void setAttackTrans(int tick) {
        getEntityData().set(ATTACKTRANS, tick);
    }

    public String getNewAnimation() {
    	return getEntityData().get(NEWANIMATION);
    }
    
    public void setNewAnimation(String animation) {
        getEntityData().set(NEWANIMATION, animation);
    }
    public String getCurrentAnimation() {
    	return getEntityData().get(CURRENTANIMATION);
    }
    
    public void setCurrentAnimation(String animation) {
        getEntityData().set(CURRENTANIMATION, animation);
    }
    public Boolean isActive() {
    	return getEntityData().get(ACTIVE);
    }
    
    public void setActive(boolean isActive) {
        getEntityData().set(ACTIVE, isActive);
        System.out.println("Active set to " + isActive);
    }
    
	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
	
	
	public <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		PacketHandler.INSTANCE.sendToServer(new sendDataToDarkKing(new String("true").getBytes(),EdataParsed.ANIMSTART.ordinal(),this.getStringUUID().getBytes()));
		event.getController().transitionLengthTicks = 10.0d;
		if(currentAnim == null)
			currentAnim = ANIM_NAME_WALK;
		
		if(event.getController() != null && event.getController().isJustStarting) {
			currentTick = 0;
		}
		if(event.getController().getCurrentAnimation() != null && event.getController().getCurrentAnimation().animationName.equalsIgnoreCase(ANIM_NAME_WALK)) {
			currentTick = 0;
		}
		if(!getNewAnimation().isEmpty()) {
			String newAnim = getNewAnimation();
			if(currentAnim == null || !currentAnim.equalsIgnoreCase(newAnim)) {
				currentAnim = newAnim;
				currentTick = 0;
			}

			PacketHandler.INSTANCE.sendToServer(new sendDataToDarkKing(currentAnim.getBytes(),EdataParsed.CURRENTANIMATION.ordinal(),this.getStringUUID().getBytes()));
			PacketHandler.INSTANCE.sendToServer(new sendDataToDarkKing(new String("").getBytes(),EdataParsed.NEWANIMRESET.ordinal(),this.getStringUUID().getBytes()));
		}

		if(currentAnim != null) {
			if(currentAnim.equalsIgnoreCase(ANIM_NAME_WALK) || currentAnim.equalsIgnoreCase(ANIM_NAME_JUMP)) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation(currentAnim,EDefaultLoopTypes.LOOP));
			}
			else if(   event.getController().getCurrentAnimation() == null || !event.getController().getCurrentAnimation().animationName.equalsIgnoreCase(currentAnim))
				event.getController().setAnimation(new AnimationBuilder().addAnimation(currentAnim,EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
		}
		if(currentAnim != null)
			PacketHandler.INSTANCE.sendToServer(new sendDataToDarkKing(currentAnim.getBytes(),EdataParsed.CURRENTANIMATION.ordinal(),this.getStringUUID().getBytes()));
		PacketHandler.INSTANCE.sendToServer(new sendDataToDarkKing( String.valueOf(currentTick).getBytes() ,EdataParsed.CURRENTTICK.ordinal(),this.getStringUUID().getBytes()));
		return PlayState.CONTINUE;
		
	}
	
	public void processAnimationUpdate(String animationID) {

		if (  getCurrentAnimation().equalsIgnoreCase("")|| !animationID.equalsIgnoreCase(this.getCurrentAnimation())) {
		    this.setNewAnimation(animationID);
		}
		
	}
	public void setAttack(EntityDataAccessor<Boolean> ATTACK_ID) {
		for(EntityDataAccessor<Boolean> ATTACK : ALL_ATTACKS) {
			getEntityData().set(ATTACK, false);
			
			if(ATTACK.getId() ==  ATTACK_ID.getId())
				getEntityData().set(ATTACK, true);
		}
		
	}
	public Boolean areAllAttacksFalse() {
		for(EntityDataAccessor<Boolean> ATTACK : ALL_ATTACKS) {
			if(getEntityData().get(ATTACK) == true) 
				return false;
		}
		return true;
	}
	
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<DarkKingEntity>(this, "controller",
                0, this::predicate));
        super.registerControllers(data);
    }
    public int secsToTick(double secs) {
    	int ticks =   Math.round((float)secs * 20);
    	if(secs <= 0 )
    		return 0;
    	
    	return ticks;
    }
    @Deprecated
    public boolean isAnimFinishedTick(int animationTick, double animationLenth) {
    	int animationLengthTicked = this.secsToTick(animationLenth);
    	
    	if(animationTick >= animationLengthTicked - 1f)
    		return true;
    	
    	return false;
    }
    public boolean isAnimFinished(int animationTick, int animationLength) {
    	if(animationTick >= animationLength) 
    		return true;
    	
    	return false;
    }
    @Override
	public void tick() {
    	
    	super.tick();
    	if (getTarget() != null && (!getTarget().isAlive() || getTarget().getHealth() <= 0)) setTarget(null);
    	
    	if( !level.isClientSide && getTarget() != null) {
    		setHasTarget(true);
    		setTargetDistance(targetDistance);
    	}
    	else if(!level.isClientSide && getTarget() == null) {
    		setHasTarget(false);
    		setTargetDistance(0);
    	}
    	
    	
    	Boolean isActive = isActive();
    	
    	if(level.isClientSide && currentAnim != null && !currentAnim.equalsIgnoreCase(ANIM_NAME_WALK)) {
    		currentTick++;
    		PacketHandler.INSTANCE.sendToServer(new sendDataToDarkKing( String.valueOf(currentTick).getBytes() ,EdataParsed.CURRENTTICK.ordinal(),this.getStringUUID().getBytes()));
    	}
    	
    	if(!level.isClientSide && getAttackTrans() > 0 && isHasTarget() && this.distanceTo(getTarget()) < random.nextDouble(5.0d)) {
    		Double randomNum = random.nextDouble(4.0d);
    		if(randomNum < 1.0d)
    			randomNum = 1.0d;
    		setAttackTrans(Math.round((float)(getAttackTrans()/randomNum)));
    		if(getAttackTrans() < 10)
    			setAttackTrans(0);
    	}
    	
    	
    	if(!level.isClientSide && getAttackTrans() > 0)
    		setAttackTrans(getAttackTrans()-1);
    	
    	if(tickCount % 2 == 0) {
    		prevX = position().x;
    		prevZ = position().z;
    	}
    	if(a == 0) {
        	if(!level.isClientSide) {
        		setActive(false);
        		processAnimationUpdate(ANIM_NAME_IDLE);
        		active = false;
        		needActivation = true;
        		needAnimStart = true;
        	}

        	a=1;
    	}
    	
    	
    	if(!level.isClientSide && waitingForRegister && isStartAnim()) {
    		waitingForRegister = false;
    		setAttack(ATTACK_TOREG);
    	}
    	
    	if(!areAllAttacksFalse() && !level.isClientSide && !waitingForRegister) {
    		if(isAttackNormal()) {
    			if(getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_SLASH1) && !didPart1) {
    				if(getCurrentTick() < secsToTick(LOOKAT_LAPSE_SLASH1)) {
    					if(tickCount % 3 == 0) {
    						if(isHasTarget()) getLookControl().setLookAt(getTarget(),30f,30f);
    					}
    					didTransitionProperly = true;
    				}
    				if(getCurrentTick() >= ATTACK_TICK_SLASH1 && isAttack && didTransitionProperly) {
    					attack((float) ATTACK_RANGE_SLASH,4f ,(float) ATTACK_ARC_SLASH, (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
    					isAttack = false;
    				}
    				
    				
    				if(isAnimFinished(getCurrentTick(), ANIM_SLASH1_TICK) && !isAttack && didTransitionProperly) { 
    					processAnimationUpdate(ANIM_NAME_SLASH2);
    					isAttack = true;
    					didTransitionProperly = false;
    					didPart1 = true;
    				}
    				
    			} else if(getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_SLASH2) && !didPart2) {
    				if(getCurrentTick() < secsToTick(LOOKAT_LAPSE_SLASH2)) {
    					if(tickCount % 3 == 0) {
    						if(isHasTarget()) getLookControl().setLookAt(getTarget(),30f,30f);
    						
    					}
    					didTransitionProperly = true;
    				}
    				if(getCurrentTick() >= ATTACK_TICK_SLASH2 && isAttack && didTransitionProperly) {
    					attack((float) ATTACK_RANGE_SLASH,4f ,(float) ATTACK_ARC_SLASH, (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
    					isAttack = false;

    				}
    				
    				
    				if(isAnimFinished(getCurrentTick(), ANIM_SLASH2_TICK) &&  !isAttack && didTransitionProperly) {
    					processAnimationUpdate(ANIM_NAME_DOWNTHURST);
    					isAttack = true;
    					didTransitionProperly = false;
    					didPart2 = true;
    				}
    				
    			} else if(getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_DOWNTHURST) && !didPart3) {
    				if(getCurrentTick() < secsToTick(LOOKAT_LAPSE_DOWNTHRUST)) {
    					if(tickCount % 2 == 0) {
    						if(isHasTarget()) getLookControl().setLookAt(getTarget(),30f,30f);
    					}
    					didTransitionProperly = true;
    				}
    				if(getCurrentTick() >= ATTACK_TICK_DOWNTHRUST && isAttack && didTransitionProperly) {
    					attack((float) ATTACK_RANGE_DOWNTHURST,2f ,(float) ATTACK_ARC_DOWNTHURST, (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 1.5f);
    					isAttack = false;
    				}
    				if(isAnimFinished(getCurrentTick(), ANIM_DOWNTHRUST_TICK)  && !isAttack && didTransitionProperly) {
    					setAttackNormal(false);
    					processAnimationUpdate(ANIM_NAME_WALK);
    					setAttackTrans(50 + random.nextInt(10));
    					isAttack = true;
    					didPart3 = false;
    					didPart2 = false;
    					didPart1 = false;
    					didTransitionProperly = false;
    				}
    					
    			} 
    		}
    		else if(isAttackGround() || getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_WALK) ){
				if(getCurrentTick() < secsToTick(LOOKAT_LAPSE_GROUNDATT)) {
					if(tickCount % 2 == 0) {
						if(isHasTarget()) getLookControl().setLookAt(getTarget());
					}
					didTransitionProperly = true;
				}
				if(getCurrentTick() >= ATTACK_TICK_GROUNDATT && isAttack && didTransitionProperly) {
					level.explode(this , getX(), getY(), getZ(), 10f, Explosion.BlockInteraction.NONE);
					isAttack = false;
				}
    			if(isAnimFinished(getCurrentTick(), ANIM_GROUNDATT_TICK) && didTransitionProperly && !isAttack) {
    				setAttackGround(false);
    				processAnimationUpdate(ANIM_NAME_WALK);
    				setAttackTrans(40+ random.nextInt(10));
    				isAttack = true;
    				didTransitionProperly = false;
    				
    			}
    		}
    		else if(isAttackLeap()) {
    			Vec3 wantedPos = new Vec3(0,0,0);
    			if( getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_JUMPPREP) && !this.didPart1) {
    				if(getCurrentTick() < ATTACK_TICK_JUMPPREP) {
    					if(isHasTarget()) { 
    						getLookControl().setLookAt(getTarget(),30f,30f);
    						getNavigation().moveTo(getTarget(), 0.6D);
    					}
    					
    					didTransitionProperly = true;
    				}
    				if(getCurrentTick() >= ATTACK_TICK_JUMPPREP && isAttack && didTransitionProperly) {
    					setDeltaMovement(0,0,0);
    					getNavigation().stop();
    					wantedPos = leapToTarget();
    					isAttack = false;
    				}
        			if(isAnimFinished(getCurrentTick(), ANIM_JUMPPREP_TICK) && didTransitionProperly && !isAttack) {
        				processAnimationUpdate(ANIM_NAME_JUMP);
        				isAttack = true;
        				didPart1 = true;
        			}
        			
    			}
    			if(getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_JUMP) && !didPart2 && distanceToSqr(wantedPos) < 3.0f * 3.0f) {
    				processAnimationUpdate(ANIM_NAME_JUMPEND);
    				didPart2 = true;
    			}
    			if(getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_JUMPEND) && !didPart3) {
    				if(isOnGround() && isAttack) {
    		    		System.out.println("ACTUAL LANDED POSITION");
    		    		System.out.println(position());
    					level.explode(this , getX(), getY(), getZ(), 7f, Explosion.BlockInteraction.NONE);
    					isAttack = false;
    				}
        			if(isAnimFinished(getCurrentTick(), ANIM_JUMPEND_TICK) && didTransitionProperly && !isAttack) {
        				setAttackLeap(false);
        				processAnimationUpdate(ANIM_NAME_WALK);
        				setAttackTrans(20 + random.nextInt(10));
        				isAttack = true;
        				didTransitionProperly = false;
        				didPart3 = false;
        				didPart1 = false;
        				didPart2 = false;
        			}
    			}
    			
    		}
    		if(getCurrentTick() > ANIM_GROUNDATT_TICK) {
    			for(EntityDataAccessor<Boolean> ATTACK : ALL_ATTACKS) {
    				getEntityData().set(ATTACK, false);
    			}
    			setAttackTrans(40+ random.nextInt(10));
				processAnimationUpdate(ANIM_NAME_WALK);
				isAttack = true;
				didTransitionProperly = false;
    		}
    	}
    	if(!level.isClientSide && areAllAttacksFalse() && ( (getTarget() == null || !getTarget().isAlive()) && isActive()  && getNavigation().isDone() ) )
    		deactivate();
    	
    	if(level.isClientSide() && getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_START) && isAnimFinished(getCurrentTick(), ANIM_START_TICK))
    		PacketHandler.INSTANCE.sendToServer(new sendDataToDarkKing(new String("true").getBytes(),EdataParsed.ACTIVATION.ordinal(),this.getStringUUID().getBytes()));
    	if(!level.isClientSide &&  isStartAnim()) {
    		if(needAnimStart == false)
    			needAnimStart = true;
    		if(needCurrentAnim == false)
    			needCurrentAnim = true;
    		if(needCurrentTick == false)
    			needCurrentTick = true;
    		
	    	if(isHasTarget())
//	    		System.out.println(getTargetDistance());
//	    	System.out.println(getCurrentAnimation());
//	    	System.out.println("start");
//	    	System.out.println(isStartAnim());
//	    	System.out.println("act");
//	    	System.out.println(isActive());
//	    	System.out.println(getCurrentTick());
//	    	System.out.println(isAttackNormal());	    	
	    	
    		if(!isActive()) {
    	    	
    			if(isHasTarget() && getTargetDistance() < 2.5f && getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_IDLE) ) {
    				processAnimationUpdate(ANIM_NAME_START);
    			}
    			else if(!getCurrentAnimation().equalsIgnoreCase(ANIM_NAME_START))
    				processAnimationUpdate(ANIM_NAME_IDLE);
    		}
    		else{
    			
    			if(areAllAttacksFalse() && isHasTarget()) {
    				if(getAttackTrans() == 0) {
    					if(getTargetDistance() > 4f &&  getTargetDistance() < 15f) {
							processAnimationUpdate(ANIM_NAME_JUMPPREP);
							waitingForRegister = true;
							ATTACK_TOREG = ATTACK_LEAP;
							isAttack = true;
    					}
    					else if(getTargetDistance() < 1.5f) {
    						double whichAttack = this.random.nextDouble(10D);
    					
    						if(whichAttack < 7D) {
    							processAnimationUpdate(ANIM_NAME_SLASH1);
    							waitingForRegister = true;
    							ATTACK_TOREG = ATTACK_NORMAL;
    							isAttack = true;
    						}
    						else if(whichAttack > 7D) {
    							processAnimationUpdate(ANIM_NAME_GROUNDATTACK);
    							waitingForRegister = true;
    							ATTACK_TOREG = ATTACK_GROUND;
    							isAttack = true;
    						}
    					}
    				}
    				else {
    					processAnimationUpdate(ANIM_NAME_WALK);
        			}
    			}
    			

    		}
    		
    		
    		setStartAnim(false);
    	}


        repelEntities(1.2F, 2.4f, 1.2F, 1.2F);
    	


 
    	
    }
    public double toMCsize(double d) {
    	return d/16f;
    }
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }
    public Vec3 leapToTarget() {
    	LivingEntity target =getTarget();
    	
    	double angleTheta = 45d;
    	double heightEntity = 0;
    	double heightTarget = 0;
    	double d = distanceTo(getTarget());
    	double xRatio;
    	double zRatio;
    	double zxTotal;
    	Vector3d landingPosition3d = new Vector3d(0,0,0); 
    	Vec2 landingPosition2d = new Vec2(0,0);
    	double range = 0;
    	double v0;
    	Vector3d initialVelocity3d = new Vector3d(0,0,0);
    	if(position().y != target.position().y) {
    		double SignE = Math.signum(position().y);
    		double SignT = Math.signum(target.position().y);
    		if(SignE != SignT) {
    			if(SignE == -1 && SignT == 1) {
    				heightEntity = 0;
    				heightTarget = Math.abs(position().y) + Math.abs(target.position().y);
    			}
    			else if(SignE == 1 && SignT == -1) {
    				heightEntity = Math.abs(position().y) + Math.abs(target.position().y);
    				heightTarget = 0;
    			}
    			else if(SignE == 0 && SignT != 0) {
    				if(SignT == -1) {
        				heightEntity = Math.abs(target.position().y);
        				heightTarget = 0;
    				}
    				else if(SignT == 1) {
    					heightEntity = 0;
    					heightTarget =  Math.abs(target.position().y);
    				}
    			}
    			else if(SignT == 0 && SignE != 0) {
    				if(SignE == -1) {
        				heightEntity = 0;
        				heightTarget = Math.abs(position().y);
    				}
    				else if(SignE == 1) {
    					heightEntity = Math.abs(position().y);
    					heightTarget = 0;
    				}
    			}
    		}
    		else if(SignE == 1 && SignT == 1) {
    			if(position().y > target.position().y) {
    				heightTarget = 0;
    				heightEntity = position().y - target.position().y;
    			}
    			else {
    				heightTarget = target.position().y - position().y;
    				heightEntity = 0;
    			}
    		}
    		else {
    			if(Math.abs(position().y) > Math.abs(target.position().y)) {
    				heightTarget = Math.abs(position().y) - Math.abs(target.position().y);
    				heightEntity = 0;
    			}
    			else {
    				heightTarget = 0;
    				heightEntity = Math.abs(target.position().y) -  Math.abs(position().y);
    			}
    		}
    		
    	}
    	else {
    		heightEntity = 0;
    		heightTarget = 0;
    	}
    	
    	if(targetAngle > angleTheta) {
    		if(targetAngle + 10 >= 90)
    			angleTheta = targetAngle;
    		else
    			angleTheta = targetAngle + 10;
    	}
    	if(angleTheta < 0) {
    		angleTheta = Math.abs(angleTheta);
    	}
    	
    	if(heightEntity != 0 || heightTarget != 0) {
    		if(heightEntity != 0) {
    			double r = Math.pow(d, 2) - Math.pow(heightEntity, 2);
    			range = Math.sqrt(r);
    		}
    		else if(heightTarget != 0) {
    			double r = Math.pow(d, 2) - Math.pow(heightTarget, 2);
    			range = Math.sqrt(r);
    		}
    	}
    	else
    		range = d;
    	if(range < 0)
    		range = Math.abs(range);
    	
    	if(position().x != target.position().x) {
        	double SignXE = Math.signum(position().x);  
        	double SignXT = Math.signum(target.position().x);
    		if(SignXE != SignXT) {
    			if(SignXE == -1 && SignXT == 1 || SignXE == 1 && SignXT == -1) {
    				landingPosition3d.x = Math.abs(position().x) + Math.abs(target.position().x);
    			}
    			else if(SignXE == 0 && SignXT != 0) {
    				if(SignXT == -1 || SignXT == 1) {
        				landingPosition3d.x = Math.abs(target.position().x);
    				}
    			}
    			else if(SignXT == 0 && SignXE != 0) {
    				if(SignXE == -1 || SignXE == 1) {
        				landingPosition3d.x = Math.abs(position().x);
    				}
    				
    			}
    		}
    		else if(SignXE == 1 && SignXT == 1) {
    			if(position().x > target.position().x) {
    				landingPosition3d.x = position().x - target.position().x;
    			}
    			else {
    				landingPosition3d.x = target.position().x - position().x;
    			}
    		}
    		else {
    			if(Math.abs(position().x) > Math.abs(target.position().x)) {
    				landingPosition3d.x = Math.abs(position().x) - Math.abs(target.position().x);
    				
    			}
    			else {
    				landingPosition3d.x = Math.abs(target.position().x) -  Math.abs(position().x);
    			}
    		}
    		
    	}
    	else {
    		landingPosition3d.x = 0;
    	}
    	
    	if(position().z != target.position().z) {
        	double SignZE = Math.signum(position().z);  
        	double SignZT = Math.signum(target.position().z);
    		if(SignZE != SignZT) {
    			if(SignZE == -1 && SignZT == 1 || SignZE == 1 && SignZT == -1) {
    				landingPosition3d.z = Math.abs(position().z) + Math.abs(target.position().z);
    			}
    			else if(SignZE == 0 && SignZT != 0) {
    				if(SignZT == -1 || SignZT == 1) {
        				landingPosition3d.z = Math.abs(target.position().z);
    				}
    			}
    			else if(SignZT == 0 && SignZE != 0) {
    				if(SignZE == -1 || SignZE == 1) {
        				landingPosition3d.z = Math.abs(position().x);
    				}
    				
    			}
    		}
    		else if(SignZE == 1 && SignZT == 1) {
    			if(position().z > target.position().z) {
    				landingPosition3d.z = position().z - target.position().z;
    			}
    			else {
    				landingPosition3d.z = target.position().z - position().z;
    			}
    		}
    		else {
    			if(Math.abs(position().z) > Math.abs(target.position().z)) {
    				landingPosition3d.z = Math.abs(position().z) - Math.abs(target.position().z);
    				
    			}
    			else {
    				landingPosition3d.z = Math.abs(target.position().z) -  Math.abs(position().z);
    			}
    		}
    		
    	}
    	else {
    		landingPosition3d.z = 0;
    	}
    		
    	landingPosition3d.y = heightTarget;
    	
    	zxTotal = landingPosition3d.x + landingPosition3d.z;
    	xRatio = landingPosition3d.x / zxTotal;
    	zRatio = landingPosition3d.z / zxTotal;
    	
    	landingPosition2d = new Vec2((float)range,(float)heightTarget);
    	
    	
    	if(heightEntity != 0) {
    		double vo = 0;
    		double phi = Math.acos(range/heightEntity);
    		vo = Math.sqrt((GRAVITY * Math.pow(range, 2)) / Math.cos((2 * angleTheta) - phi) * Math.sqrt(heightEntity + Math.pow(heightEntity, 2)) + heightEntity );
    		v0 = vo;
    		
    		double vox = vo * Math.cos(angleTheta);
    		double voy = vo * Math.sin(angleTheta);
    		
    		initialVelocity3d.x = vox * xRatio;
    		initialVelocity3d.z = vox * zRatio;
    		initialVelocity3d.y = voy;
    	}
    	else if(heightTarget != 0) {
    		double vo = 0;
    		
    		
    		vo = range / Math.cos(angleTheta) * (Math.sqrt( (heightTarget - Math.sin(angleTheta) * (range / Math.cos(angleTheta))) / -0.5f * GRAVITY ));
    		v0 = vo;
    		
    		double vox = vo * Math.cos(angleTheta);
    		double voy = vo * Math.sin(angleTheta);
    		
    		initialVelocity3d.x = vox * xRatio;
    		initialVelocity3d.z = vox * zRatio;
    		initialVelocity3d.y = voy;
    	}
    	
    	else {
    		double vo = 0;
    		
    		vo = Math.sqrt((GRAVITY * range)/Math.sin(angleTheta/0.5f));
    		v0 = vo;
    		
    		double vox = vo * Math.cos(angleTheta);
    		double voy = vo * Math.sin(angleTheta);
    		
    		initialVelocity3d.x = vox * xRatio;
    		initialVelocity3d.z = vox * zRatio;
    		initialVelocity3d.y = voy;
    	}
    	double zSign = Math.signum(target.position().z);
    	double xSign = Math.signum(target.position().x);
    	initialVelocity3d.x = initialVelocity3d.x;
    	initialVelocity3d.z = initialVelocity3d.z;
    	
    	if(zSign < 0) {
    		if(target.position().z > position().z)
    			initialVelocity3d.z = Math.abs(initialVelocity3d.z);
    		else if(target.position().z < position().z)
    			initialVelocity3d.z = Math.abs(initialVelocity3d.z) * -1;
    	}
    	else if(zSign > 0) {
    		if(target.position().z < position().z)
    			initialVelocity3d.z = Math.abs(initialVelocity3d.z) * -1;
    		else if(target.position().z > position().z)
    			initialVelocity3d.z = Math.abs(initialVelocity3d.z);
    	}
    	
    	if(xSign < 0) {
    		if(target.position().x > position().x)
    			initialVelocity3d.x = Math.abs(initialVelocity3d.x);
    		else if(target.position().x < position().x)
    			initialVelocity3d.x = Math.abs(initialVelocity3d.x) * -1;
    	}
    	else if(xSign > 0) {
    		if(target.position().x < position().x)
    			initialVelocity3d.x = Math.abs(initialVelocity3d.x) * -1;
    		else if(target.position().x > position().x)
    			initialVelocity3d.x = Math.abs(initialVelocity3d.x);
    	}
    	
    	int timeInTheAir = (int) ((int) (2 * v0 * Math.sin(angleTheta)) / GRAVITY);
    	
    	double airResistanceCompensationX = 0;
    	double airResistanceCompensationZ = 0;
    	double airResistanceCompensationY = 0;
    	for(int i = 0; i < timeInTheAir; i++) {
    		double vx = initialVelocity3d.x + i;
    		airResistanceCompensationX += vx * 0.02d;
    		
    		double vz = initialVelocity3d.z + i;
    		airResistanceCompensationZ += vz * 0.02d;
    		
    		double vy = initialVelocity3d.y + ((GRAVITY * -1) * i);
    		airResistanceCompensationY += vy * 0.02d;
    	}
    	
    	
    	this.setDeltaMovement(initialVelocity3d.x + airResistanceCompensationX * Math.signum(initialVelocity3d.x) ,initialVelocity3d.y + airResistanceCompensationY * Math.signum(initialVelocity3d.y),initialVelocity3d.z + airResistanceCompensationZ * Math.signum(initialVelocity3d.z));
    	System.out.println("STARTING POSITION:");
    	System.out.println(this.position());
    	System.out.println("WANTED LANDING POSITION:");
    	System.out.println(target.position());
    	
    	return target.position();
    }
    public Player getNearestPlayerInList(List<Player> PlayerList){
    	Player nearestEntity = null;
    	
    	for(Player currentEntity : PlayerList) {
    		if(currentEntity.getAbilities().instabuild)
    			continue;
    		
    		if(nearestEntity == null) 
    			nearestEntity = currentEntity;
    		
    		if(nearestEntity.distanceTo(this) > currentEntity.distanceTo(this) )
    			nearestEntity = currentEntity;
    		
    	}
    	
    	return nearestEntity;
    	
    }
    public void attack(float range, float applyKnockback,float arc, float damagePassed) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, 3, range, range);
        float damage = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yBodyRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                entityHit.hurt(DamageSource.mobAttack(this), damage * 1.5F);
                if (entityHit.isBlocking()) entityHit.getUseItem().hurtAndBreak(400, entityHit, player -> player.broadcastBreakEvent(entityHit.getUsedItemHand()));
                entityHit.setDeltaMovement(entityHit.getDeltaMovement().x * applyKnockback, entityHit.getDeltaMovement().y, entityHit.getDeltaMovement().z * applyKnockback);
            }
        }
    }
    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        super.writeSpawnData(buf);
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
    	 Entity entitySource = source.getEntity();
    	 
    	 if(entitySource != null) {
    		 if(this.isActive()) {
    			 if(source.isFall()  || this.distanceTo(entitySource) > 25f) { 
    				 return false;
    			 }
    			 
    			 if(getTarget() == null || entitySource != getTarget()) {
    				 double preventConstantSwitch = random.nextDouble(10D);
    				 
    				 if( amount < getHealth() && (amount/ getHealth()) * 100f > 5f ) {
    					 if(entitySource instanceof LivingEntity && preventConstantSwitch > 3D )
    						 setTarget((LivingEntity)entitySource);
    				 }
    				 else if (amount < getHealth() && (amount/ getHealth()) * 100f < 5f ) {
    					 if(entitySource instanceof LivingEntity && preventConstantSwitch < 3D)
    						 setTarget((LivingEntity)entitySource);
    				 }
    			 }
    			 return super.hurt(source,amount);
    		 }
    	 }
    	 return false;
    }
    
    public void activate() {
    	setActivating(false);
    	this.active = true;
    	setActive(true);
    	this.setAttackTrans(20 + random.nextInt(30));
    	
    }
    
    public void deactivate() {
    	this.active = false;
    	setActive(false);
		processAnimationUpdate(ANIM_NAME_IDLE);
		needActivation = true;
    }

    @Override
    protected boolean hasBossBar() {
        return true;
    }
    
    @Override
    protected BossEvent.BossBarColor bossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        return false;
    }
    
    @Override
    protected PathNavigation createNavigation(Level world) {
        return new MMPathNavigateGround(this, world);
    }
    
    @Override
    protected void repelEntities(float x, float y, float z, float radius) {
        super.repelEntities(x, y, z, radius);
    }
    
    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
    	return sizeIn.height * 0.98F;
    }
    
    @Override
    protected BodyRotationControl createBodyControl() {
        return new SmartBodyHelper(this);
    }
    
    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }
    @Override
    public boolean fireImmune() {
    	return true;
    	
    }
    @Override 
    public boolean ignoreExplosion() {
    	return true;
    }

   

}
