package net.smileycorp.magiadaemonica.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.atlas.api.block.BlockProperties;
import net.smileycorp.atlas.api.client.MetaStateMapper;
import net.smileycorp.atlas.api.item.IMetaItem;
import net.smileycorp.magiadaemonica.common.CommonProxy;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.common.items.DaemonicaItems;

@EventBusSubscriber(value = Side.CLIENT, modid= Constants.MODID)
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		DaemonicaBlocks.CHALK.registerModels();
		ModelLoader.setCustomStateMapper(DaemonicaBlocks.FLOWER, new MetaStateMapper());
		for (Item item : DaemonicaItems.ITEMS) if (item instanceof IMetaItem &! (item instanceof ItemBlock &&
				((BlockProperties)((ItemBlock) item).getBlock()).usesCustomItemHandler())) {
			if (((IMetaItem) item).getMaxMeta() > 0) for (int i = 0; i < ((IMetaItem) item).getMaxMeta(); i++) {
				ModelResourceLocation loc = new ModelResourceLocation(Constants.locStr(((IMetaItem) item).byMeta(i)));
				ModelLoader.setCustomModelResourceLocation(item, i, loc);
			}
			else {
				ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName().toString());
				ModelLoader.setCustomModelResourceLocation(item, 0, loc);
			}
		}
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(DaemonicaBlocks.FLOWER), 0,
				new ModelResourceLocation(Constants.locStr("lavender"), "inventory"));
	}
	
}
