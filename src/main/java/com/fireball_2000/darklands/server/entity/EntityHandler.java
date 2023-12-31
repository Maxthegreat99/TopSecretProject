package com.fireball_2000.darklands.server.entity;

import com.fireball_2000.darklands.DarkLands;
import com.fireball_2000.darklands.server.entity.custom.DarkKingEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = DarkLands.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityHandler {
	public static final DeferredRegister<EntityType<?>> REG = DeferredRegister.create(ForgeRegistries.ENTITIES, DarkLands.MOD_ID);
	
	public static final RegistryObject<EntityType<DarkKingEntity>> DARKKING = REG.register("dark_king", () -> EntityType.Builder.of(DarkKingEntity::new, MobCategory.MONSTER).sized(1.2f, 2.4f).setUpdateInterval(1).build(new ResourceLocation(DarkLands.MOD_ID, "dark_king").toString()));


    @SubscribeEvent
    public static void onCreateAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityHandler.DARKKING.get(), DarkKingEntity.createAttributes().build());
    	
    }

}
