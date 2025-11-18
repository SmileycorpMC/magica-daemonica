package net.smileycorp.magiadaemonica.common.rituals.summoning.trades.prices;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentBase;

public interface Price {

    void pay(EntityPlayer player, int tier);

    boolean canPay(EntityPlayer player, int tier);

    TextComponentBase getDescription(int tier);

}
