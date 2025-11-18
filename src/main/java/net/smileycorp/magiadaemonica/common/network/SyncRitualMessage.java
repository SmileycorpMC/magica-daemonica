package net.smileycorp.magiadaemonica.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.magiadaemonica.client.rituals.RitualsClient;
import net.smileycorp.magiadaemonica.common.rituals.Ritual;
import net.smileycorp.magiadaemonica.common.rituals.RitualsRegistry;

public class SyncRitualMessage implements IMessage {

    private Ritual ritual;

    public SyncRitualMessage() {}

    public SyncRitualMessage(Ritual ritual) {
        this.ritual = ritual;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ritual = RitualsRegistry.getRitualFromNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound nbt = ritual.writeToNBT();
        nbt.setString("id", ritual.getID().toString());
        ByteBufUtils.writeTag(buf, nbt);
    }

    public IMessage process(MessageContext ctx) {
        if (ctx.side == Side.CLIENT) Minecraft.getMinecraft().addScheduledTask(() -> RitualsClient.getInstance().addRitual(ritual));
        return null;
    }

}
