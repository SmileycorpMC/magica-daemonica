package net.smileycorp.magiadaemonica.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.smileycorp.magiadaemonica.common.Constants;

public class ItemDaemonicaFood extends ItemDaemonicaEdible {

    public ItemDaemonicaFood() {
        super("food", 1, 0.2f);
        setHasSubtypes(true);
    }
    
    @Override
    public String byMeta(int meta) {
        return Variant.get(meta).getName();
    }
    
    @Override
    public int getMaxMeta() {
        return Variant.values().length;
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) return;
        for (int i = 0; i < Variant.values().length; i++) items.add(new ItemStack(this, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + Constants.name(byMeta(stack.getMetadata()));
    }

    @Override
    public int getHealAmount(ItemStack stack) {
        return Variant.get(stack.getMetadata()).getHunger();
    }

    @Override
    public float getSaturationModifier(ItemStack stack) {
        return Variant.get(stack.getMetadata()).getSaturation();
    }

    public enum Variant {
        SUET("suet", 1, 0.2f),
        TALLOW("tallow", 3, 0.4f);

        private final String name;
        private final int hunger;
        private final float saturation;

        Variant(String name, int hunger, float saturation) {
            this.name = name;
            this.hunger = hunger;
            this.saturation = saturation;
        }

        public String getName() {
            return name;
        }

        public int getHunger() {
            return hunger;
        }

        public float getSaturation() {
            return saturation;
        }

        public static Variant get(int meta) {
            return meta < values().length ? values()[meta] : SUET;
        }

    }

}