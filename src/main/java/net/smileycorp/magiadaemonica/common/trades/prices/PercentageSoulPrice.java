package net.smileycorp.magiadaemonica.common.trades.prices;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentBase;
import net.smileycorp.magiadaemonica.common.capabilities.DaemonicaCapabilities;
import net.smileycorp.magiadaemonica.common.network.SyncSoulMessage;

public class PercentageSoulPrice implements Price {

    private final float amount;

    public PercentageSoulPrice(float amount) {
        this.amount = amount;
    }

    @Override
    public void pay(EntityPlayer player, int tier) {
        if (!player.hasCapability(DaemonicaCapabilities.SOUL, null)) return;
        player.getCapability(DaemonicaCapabilities.SOUL, null).consumeSoul(amount, false);
        if (player instanceof EntityPlayerMP) SyncSoulMessage.send((EntityPlayerMP) player);
    }

    @Override
    public boolean canPay(EntityPlayer player, int tier) {
        if (!player.hasCapability(DaemonicaCapabilities.SOUL, null)) return false;
        return player.getCapability(DaemonicaCapabilities.SOUL, null).getSoul() > 0;
    }

    @Override
    public TextComponentBase getDescription(int tier) {
        return null;
    }

}
