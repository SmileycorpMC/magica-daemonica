package net.smileycorp.magiadaemonica.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.smileycorp.magiadaemonica.common.Constants;

public class PacketHandler {

	public static final SimpleNetworkWrapper NETWORK_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);

	public static void initPackets() {
		int id = 0;
		NETWORK_INSTANCE.registerMessage(SyncSoulMessage::process, SyncSoulMessage.class, id++, Side.CLIENT);
		NETWORK_INSTANCE.registerMessage(SyncRitualMessage::process, SyncRitualMessage.class, id++, Side.CLIENT);
		NETWORK_INSTANCE.registerMessage(RemoveRitualMessage::process, RemoveRitualMessage.class, id++, Side.CLIENT);
	}

}
