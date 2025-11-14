package net.smileycorp.magiadaemonica.common.items;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.smileycorp.atlas.api.block.BlockProperties;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;

import java.util.List;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class DaemonicaItems {

    public static final List<Item> ITEMS = Lists.newArrayList();

    public static final ItemChalkStick CHALK_STICK = new ItemChalkStick();
    public static final ItemDaemonicaFood MATERIAL = new ItemDaemonicaFood();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (Block block : DaemonicaBlocks.BLOCKS) if (block instanceof BlockProperties) register(registry, new ItemDaemonicaBlock(block));
        DaemonicaBlocks.CHALK.registerItems(registry);
        register(registry, CHALK_STICK);
        register(registry, MATERIAL);
    }

    private static <T extends Item> void register(IForgeRegistry<Item> registry, T item) {
        registry.register(item);
        ITEMS.add(item);
    }

}
