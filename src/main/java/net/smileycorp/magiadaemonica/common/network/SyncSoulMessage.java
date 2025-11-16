package net.smileycorp.magiadaemonica.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.magiadaemonica.client.NetworkClientHandler;
import net.smileycorp.magiadaemonica.common.capabilities.DaemonicaCapabilities;

public class SyncSoulMessage implements IMessage {

    private float soul;

    public SyncSoulMessage() {}

    public SyncSoulMessage(float soul) {
        this.soul = soul;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        soul = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(soul);
    }

    public IMessage process(MessageContext ctx) {
        if (ctx.side == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(() -> NetworkClientHandler.setSoul(soul));
        return null;
    }

    public static void send(EntityPlayerMP player) {
        if (!player.hasCapability(DaemonicaCapabilities.SOUL ,null)) return;
        PacketHandler.NETWORK_INSTANCE.sendTo(new SyncSoulMessage(player.getCapability(DaemonicaCapabilities.SOUL, null).getSoul()), player);
    }

}
