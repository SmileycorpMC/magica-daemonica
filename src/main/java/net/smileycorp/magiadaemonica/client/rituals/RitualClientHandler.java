package net.smileycorp.magiadaemonica.client.rituals;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.magiadaemonica.common.rituals.IRitual;
import net.smileycorp.magiadaemonica.common.rituals.summoningcircle.SummoningCircle;

import java.util.Map;
import java.util.Set;

public class RitualClientHandler {

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
            if (!world.isBlockLoaded(ritual.getCenter())) {
                toRemove.add(ritual.getCenter());
                continue;
            }
            renderRitual(ritual);
        }
        for (BlockPos pos : toRemove) rituals.remove(pos);
    }

    public static void addRitual(IRitual ritual) {
        if (ritual == null) return;
        rituals.put(ritual.getCenter(), ritual);
    }

    public static void removeRitual(BlockPos pos) {
        rituals.remove(pos);
    }

    public static void clear() {
        rituals.clear();
    }

    public static void renderRitual(IRitual ritual) {
        if (ritual == null) return;
        RitualRenderer renderer = RENDERERS.get(ritual.getID());
        if (renderer == null) return;
        int width = ritual.getWidth();
        int height = ritual.getHeight();
        BlockPos pos = ritual.getPos();
        GlStateManager.pushMatrix();
        applyTransformations(ritual);
        renderer.render(ritual, pos.getX() - TileEntityRendererDispatcher.staticPlayerX,
                pos.getY() - TileEntityRendererDispatcher.staticPlayerY,
                pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ, width, height);
        GlStateManager.popMatrix();
    }

    public static void applyTransformations(IRitual ritual) {
        BlockPos pos = ritual.getPos();
        int width = ritual.getWidth();
        int height = ritual.getHeight();
        GlStateManager.translate(pos.getX() - TileEntityRendererDispatcher.staticPlayerX,
                pos.getY() - TileEntityRendererDispatcher.staticPlayerY,
                pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);
        switch (ritual.getRotation()) {
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
    }

    public static void clientTick() {
        Set<BlockPos> toRemove = Sets.newHashSet();
        WorldClient world = Minecraft.getMinecraft().world;
        for (IRitual ritual : rituals.values()) {
            if (!world.isBlockLoaded(ritual.getCenter())) {
                toRemove.add(ritual.getCenter());
                continue;
            }
            ritual.clientTick(world);
        }
        for (BlockPos pos : toRemove) rituals.remove(pos);
    }

}
