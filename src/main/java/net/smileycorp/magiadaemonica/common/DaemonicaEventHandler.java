package net.smileycorp.magiadaemonica.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.smileycorp.magiadaemonica.common.capabilities.DaemonicaCapabilities;
import net.smileycorp.magiadaemonica.common.capabilities.ISoul;
import net.smileycorp.magiadaemonica.common.network.SyncSoulMessage;
import net.smileycorp.magiadaemonica.common.rituals.WorldDataRituals;

public class DaemonicaEventHandler {

	@SubscribeEvent
	public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (!(entity instanceof EntityPlayer)) return;
		if (entity.hasCapability(DaemonicaCapabilities.SOUL, null)) return;
		event.addCapability(Constants.loc("soul"), new ISoul.Provider());
	}

	@SubscribeEvent
	public void join(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player == null) return;
		if (!(event.player instanceof EntityPlayerMP)) return;
		SyncSoulMessage.send((EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void clone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
		EntityPlayer original = event.getOriginal();
		EntityPlayer player = event.getEntityPlayer();
		if (!original.hasCapability(DaemonicaCapabilities.SOUL, null) |!
				player.hasCapability(DaemonicaCapabilities.SOUL, null)) return;
		player.getCapability(DaemonicaCapabilities.SOUL, null).setSoul(
				original.getCapability(DaemonicaCapabilities.SOUL, null).getSoul());
		if (!(player instanceof EntityPlayerMP)) return;
		SyncSoulMessage.send((EntityPlayerMP) player);
	}

	@SubscribeEvent
	public void startTrackingChunk(ChunkWatchEvent.Watch event) {
		Chunk chunk = event.getChunkInstance();
		if (chunk == null) return;
		World world = chunk.getWorld();
		if (!(world instanceof WorldServer)) return;
		WorldDataRituals.get((WorldServer) world).syncRituals(chunk);
	}
	
}
