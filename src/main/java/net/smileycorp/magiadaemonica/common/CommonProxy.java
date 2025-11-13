package net.smileycorp.magiadaemonica.common;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.smileycorp.magiadaemonica.common.capabilities.ISoul;
import net.smileycorp.magiadaemonica.common.command.CommandSoul;
import net.smileycorp.magiadaemonica.common.network.PacketHandler;
import net.smileycorp.magiadaemonica.common.world.DaemonicaWorldGen;
import net.smileycorp.magiadaemonica.config.WorldConfig;
import net.smileycorp.magiadaemonica.integration.FutureMCIntegration;

@Mod.EventBusSubscriber
public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		WorldConfig.syncConfig(event);
		MinecraftForge.EVENT_BUS.register(new DaemonicaEventHandler());
		PacketHandler.initPackets();
		CapabilityManager.INSTANCE.register(ISoul.class, new ISoul.Storage(), ISoul.Soul::new);
		GameRegistry.registerWorldGenerator(new DaemonicaWorldGen(), 99);
	}

	public void init(FMLInitializationEvent event) {

	}
	
	public void postInit(FMLPostInitializationEvent event) {

	}

	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSoul());
	}

	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		if (Loader.isModLoaded("futuremc")) FutureMCIntegration.registerRecipes();
	}

}
