package com.fireball_2000.darklands.server.packets;

import com.fireball_2000.darklands.DarkLands;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


public class PacketHandler {
	
    public static SimpleChannel INSTANCE;
    public static int packetId = 0;
    
    public static int id() {
    	return packetId++;
    }

    public static void init() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(DarkLands.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        
        INSTANCE = net;
        INSTANCE.messageBuilder(sendDataToDarkKing.class, id(), NetworkDirection.PLAY_TO_SERVER)
        .encoder(sendDataToDarkKing::encode)
        .decoder(sendDataToDarkKing::new)
        .consumer(sendDataToDarkKing::handle).add();

    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
   }

   public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
       INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
   }
	
	
	
}


