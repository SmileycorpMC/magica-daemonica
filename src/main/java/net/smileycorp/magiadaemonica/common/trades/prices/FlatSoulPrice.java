package net.smileycorp.magiadaemonica.common.trades.prices;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentBase;
import net.smileycorp.magiadaemonica.common.capabilities.DaemonicaCapabilities;
import net.smileycorp.magiadaemonica.common.network.SyncSoulMessage;

public class FlatSoulPrice implements Price {

    private final float amount;

    public FlatSoulPrice(float amount) {
        this.amount = amount;
    }

    @Override
    public void pay(EntityPlayer player, int tier) {
        if (!player.hasCapability(DaemonicaCapabilities.SOUL, null)) return;
        player.getCapability(DaemonicaCapabilities.SOUL, null).consumeSoul(amount, true);
        if (player instanceof EntityPlayerMP) SyncSoulMessage.send((EntityPlayerMP) player);
    }

    @Override
    public boolean canPay(EntityPlayer player, int tier) {
        if (!player.hasCapability(DaemonicaCapabilities.SOUL, null)) return false;
        return player.getCapability(DaemonicaCapabilities.SOUL, null).getSoul() >= amount;
    }

    @Override
    public TextComponentBase getDescription(int tier) {
        return null;
    }

}
