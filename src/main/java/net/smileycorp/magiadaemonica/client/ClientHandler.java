package net.smileycorp.magiadaemonica.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.smileycorp.magiadaemonica.common.capabilities.DaemonicaCapabilities;

public class ClientHandler {

    public static void setSoul(float soul) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        System.out.println(player);
        if (!player.hasCapability(DaemonicaCapabilities.SOUL, null)) return;
        System.out.println(soul);
        player.getCapability(DaemonicaCapabilities.SOUL, null).setSoul(soul);
    }

}
