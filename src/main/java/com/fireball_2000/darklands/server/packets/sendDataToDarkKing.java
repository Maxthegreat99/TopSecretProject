package com.fireball_2000.darklands.server.packets;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.fireball_2000.darklands.server.entity.custom.DarkKingEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class sendDataToDarkKing {

	String dataStored;
	EdataParsed dataTypeSent;
	String entityToManipulate;
	
	public sendDataToDarkKing(byte[] String, int dataParsed, byte[] entityId){
		dataStored = new String(String, StandardCharsets.UTF_8);
		entityToManipulate =  new String(entityId, StandardCharsets.UTF_8);
		for(EdataParsed potentialData : EdataParsed.values()){
			if(potentialData.ordinal() == dataParsed) {
				dataTypeSent = potentialData;
			}
		}
	}
	
    public sendDataToDarkKing(FriendlyByteBuf buf) {
    	this(buf.readByteArray(), buf.readInt(), buf.readByteArray());
    }

	
    public void encode(FriendlyByteBuf buf) {
    	buf.writeByteArray(dataStored.getBytes());
    	buf.writeInt(dataTypeSent.ordinal());
    	buf.writeByteArray(entityToManipulate.getBytes());
    }
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
    	NetworkEvent.Context context = supplier.get();
        final var success = new AtomicBoolean(false);
        context.enqueueWork(() -> {
        	ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();
            
            List<DarkKingEntity> darkKings = getEntitiesNearby(DarkKingEntity.class,50d,50d,50d, 50d,player,level);
            if(darkKings.size() > 0) {
	            for(DarkKingEntity darkKing : darkKings) {
	            	if(darkKing.getStringUUID().equalsIgnoreCase(entityToManipulate)) {
//		            	System.out.println("forloop");
	            		if(dataTypeSent == EdataParsed.ACTIVATION && darkKing.needActivation ) {
//	            			System.out.println("activate");
	            			darkKing.activate();
	            			darkKing.needActivation = false;
	            			success.set(true);
	            		}
	            		else if(dataTypeSent == EdataParsed.ANIMSTART && darkKing.needAnimStart) {
	            			darkKing.setStartAnim(true);
	            			darkKing.needAnimStart = false;
//	            			System.out.println("animStart");
	            			success.set(true);
	            		}
	            		else if(dataTypeSent == EdataParsed.CURRENTANIMATION && darkKing.needCurrentAnim) {
	            			darkKing.setCurrentAnimation(this.dataStored);
	            			darkKing.needCurrentAnim = false;
//	            			System.out.println("currentanim");
	            			success.set(true);
	            		}
	            		else if(dataTypeSent == EdataParsed.CURRENTTICK && darkKing.needCurrentTick) {
	            			darkKing.setCurrentTick(Integer.parseInt(dataStored));
	            			darkKing.needCurrentTick = false;
//	            			System.out.println("currenttick");
	            			success.set(true);
	            		}
	            		else if(dataTypeSent == EdataParsed.DEACTIVATION && darkKing.needDeavation) {
	            			darkKing.deactivate();
	            			darkKing.needDeavation = false;
//	            			System.out.println("deac");
	            			success.set(true);
	            		}
	            		else if(dataTypeSent == EdataParsed.NEWANIMRESET) {
	            			darkKing.setNewAnimation("");
//	            			System.out.println("newAnim");
	            			success.set(true);
	            		}
	            	}
	            }
            }
        });

        context.setPacketHandled(true);
        return success.get();
    }
    
    public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r, ServerPlayer player , ServerLevel level) {
        return level.getEntitiesOfClass(entityClass, player.getBoundingBox().inflate(dX, dY, dZ), e -> e != player && player.distanceTo(e) <= r + e.getBbWidth() / 2f && e.getY() <= player.getY() + dY);
    }
}




