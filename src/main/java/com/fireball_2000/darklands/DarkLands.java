package com.fireball_2000.darklands;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib3.GeckoLib;





@Mod("darklands")
public class DarkLands {
	public static final String MOD_ID = "darklands";
	public DarkLands() {
		
		GeckoLib.initialize();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
}