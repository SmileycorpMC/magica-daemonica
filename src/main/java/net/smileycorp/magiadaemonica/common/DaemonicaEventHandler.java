package net.smileycorp.magiadaemonica.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.magiadaemonica.common.capabilities.DaemonicaCapabilities;
import net.smileycorp.magiadaemonica.common.capabilities.Soul;
import net.smileycorp.magiadaemonica.common.network.SyncSoulMessage;
import net.smileycorp.magiadaemonica.common.rituals.Ritual;
import net.smileycorp.magiadaemonica.common.rituals.Rituals;
import net.smileycorp.magiadaemonica.common.rituals.RitualsServer;

public class DaemonicaEventHandler {

	@SubscribeEvent
	public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (!(entity instanceof EntityPlayer)) return;
		if (entity.hasCapability(DaemonicaCapabilities.SOUL, null)) return;
		event.addCapability(Constants.loc("soul"), new Soul.Provider());
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
		RitualsServer.get((WorldServer) world).syncRituals(chunk);
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;
		Rituals.get(event.world).tick();
	}

	//lowest priority so other events can cancel the event if they need to
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void death(LivingDeathEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null) return;
		if (entity.world.isRemote) return;
		if (entity instanceof IMob || entity instanceof EntityPlayer) return;
		if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
		Ritual ritual = Rituals.get(entity.world).getRitual(entity.posX, entity.posY, entity.posZ, 2);
		if (ritual == null) return;
		if (ritual.isActive()) return;
		ritual.addPower((int) (entity.getMaxHealth() * 10));
	}
	
}
