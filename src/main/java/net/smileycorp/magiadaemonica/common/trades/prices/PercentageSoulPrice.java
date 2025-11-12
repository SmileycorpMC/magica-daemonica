package net.smileycorp.magiadaemonica.common.trades.prices;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentBase;
import net.smileycorp.magiadaemonica.common.MagiaDaemonicaCapabilities;

public class PercentageSoulPrice implements Price {

    private final float amount;

    public PercentageSoulPrice(float amount) {
        this.amount = amount;
    }

    @Override
    public void pay(EntityPlayer player, int tier) {
        if (!player.hasCapability(MagiaDaemonicaCapabilities.SOUL, null)) return;
        player.getCapability(MagiaDaemonicaCapabilities.SOUL, null).consumeSoul(amount, false);
    }

    @Override
    public boolean canPay(EntityPlayer player, int tier) {
        if (!player.hasCapability(MagiaDaemonicaCapabilities.SOUL, null)) return false;
        return player.getCapability(MagiaDaemonicaCapabilities.SOUL, null).getSoul() > 0;
    }

    @Override
    public TextComponentBase getDescription(int tier) {
        return null;
    }

}
