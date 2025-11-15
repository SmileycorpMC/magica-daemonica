package net.smileycorp.magiadaemonica.client;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.smileycorp.magiadaemonica.client.rituals.RitualRendererDispatcher;

public class ClientEventHandler {

    @SubscribeEvent
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        RitualRendererDispatcher.renderRituals();
    }

    @SubscribeEvent
    public void logOut(PlayerEvent.PlayerLoggedOutEvent event) {
        RitualRendererDispatcher.clear();
    }

}
