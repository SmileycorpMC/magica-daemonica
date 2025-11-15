package net.smileycorp.magiadaemonica.client.rituals;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.magiadaemonica.common.rituals.IRitual;
import net.smileycorp.magiadaemonica.common.rituals.summoningcircle.SummoningCircle;

import java.util.Map;
import java.util.Set;

public class RitualRendererDispatcher {

    private static final Map<ResourceLocation, RitualRenderer> RENDERERS = Maps.newHashMap();
    private static final Map<BlockPos, IRitual> rituals = Maps.newHashMap();

    static {
        registerRitualRenderer(SummoningCircle.ID, new SummoningCircleRenderer());
    }

    public static void registerRitualRenderer(ResourceLocation id, RitualRenderer renderer) {
        RENDERERS.put(id, renderer);
    }

    public static void renderRituals() {
        Set<BlockPos> toRemove = Sets.newHashSet();
        WorldClient world = Minecraft.getMinecraft().world;
        for (IRitual ritual : rituals.values()) {
            if (!world.isBlockLoaded(ritual.getPos()) &! world.isBlockLoaded(ritual.getPos().add(ritual.getHeight(), 0, ritual.getWidth()))) {
                toRemove.add(ritual.getPos());
                continue;
            }
            renderRitual(ritual);
        }
        for (BlockPos pos : toRemove) rituals.remove(pos);
    }

    public static void addRitual(IRitual ritual) {
        if (ritual == null) return;
        rituals.put(ritual.getPos(), ritual);
    }

    public static void removeRitual(BlockPos pos) {
        rituals.remove(pos);
    }

    public static void clear() {
        rituals.clear();
    }

    public static void renderRitual(IRitual ritual) {
        Minecraft mc = Minecraft.getMinecraft();
        if (ritual == null) return;
        RitualRenderer renderer = RENDERERS.get(ritual.getID());
        if (renderer == null) return;
        float partialTicks = mc.getRenderPartialTicks();
        int width = ritual.getWidth();
        int height = ritual.getHeight();
        BlockPos pos = ritual.getPos();
        GlStateManager.pushMatrix();
        Entity entity = mc.getRenderViewEntity();
        GlStateManager.translate(pos.getX() - (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks),
                pos.getY() - (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks),
                pos.getZ() - (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks));
        switch (ritual.getFacing()) {
            case EAST:
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.translate(-width, 0, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-width, 0, -height);
                break;
            case WEST:
                GlStateManager.rotate(-90, 0, 1, 0);
                GlStateManager.translate(0, 0, -height);
                break;
        }
        renderer.render(ritual, width, height);
        GlStateManager.popMatrix();
    }

}
