package net.smileycorp.magiadaemonica.common.data.prices;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentBase;
import net.smileycorp.magiadaemonica.common.PriceUtils;

public class FlatHealthPrice implements Price {

    private final float amount;

    public FlatHealthPrice(float amount) {
        this.amount = amount;
    }

    @Override
    public void pay(EntityPlayer player, int tier) {
        PriceUtils.addAttribute(player, SharedMonsterAttributes.MAX_HEALTH, amount);
    }

    @Override
    public boolean canPay(EntityPlayer player, int tier) {
        return player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue() > amount;
    }

    @Override
    public TextComponentBase getDescription(int tier) {
        return null;
    }

}
