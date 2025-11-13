package net.smileycorp.magiadaemonica.common;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;

public class DaemonicaTab extends CreativeTabs {
    
    private ItemStack stack;
    private boolean needsRefresh = true;
    
    public DaemonicaTab() {
        super(Constants.name("tab"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(DaemonicaBlocks.CHALK.getBase());
    }
    
}
