package com.fireball_2000.darklands;

import com.bobmowzie.mowziesmobs.client.model.tools.MowzieGeoBuilder;
import com.fireball_2000.darklands.client.renderers.DarkKingRenderer;
import com.fireball_2000.darklands.server.entity.EntityHandler;
import com.fireball_2000.darklands.server.packets.PacketHandler;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

@Mod("darklands")
public class DarkLands {
	public static final String MOD_ID = "darklands";	
	
	public DarkLands() {
		
        MowzieGeoBuilder.registerGeoBuilder(MOD_ID, new MowzieGeoBuilder());
		GeckoLib.initialize();
		
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
        EntityHandler.REG.register(bus);
        
        bus.addListener(this::commonSetup);
		
        bus.addListener(this::clientSetup);
        
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	 private void clientSetup(final FMLClientSetupEvent event) {
		 EntityRenderers.register(EntityHandler.DARKKING.get(), DarkKingRenderer::new);
	 }
	 
	 private void commonSetup(final FMLCommonSetupEvent event) {

		 PacketHandler.init();
	        
	 }
	 
	 
	 
}