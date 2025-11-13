package net.smileycorp.magiadaemonica.common.items;

import net.minecraft.item.ItemFood;
import net.smileycorp.atlas.api.item.IMetaItem;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.MagiaDaemonica;

public class ItemDaemonicaFood extends ItemFood implements IMetaItem {

    public ItemDaemonicaFood(String name, int hunger, float saturation) {
        super(hunger, saturation, false);
        setRegistryName(Constants.loc(name));
        setUnlocalizedName(Constants.name(name));
        setCreativeTab(MagiaDaemonica.CREATIVE_TAB);
    }
    
}
