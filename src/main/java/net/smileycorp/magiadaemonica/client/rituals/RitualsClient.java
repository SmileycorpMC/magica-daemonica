package net.smileycorp.magiadaemonica.client.rituals;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.magiadaemonica.client.rituals.renderers.RitualRenderer;
import net.smileycorp.magiadaemonica.client.rituals.renderers.SummoningCircleRenderer;
import net.smileycorp.magiadaemonica.common.rituals.Ritual;
import net.smileycorp.magiadaemonica.common.rituals.Rituals;
import net.smileycorp.magiadaemonica.common.rituals.summoning.SummoningCircle;

import java.util.Map;
import java.util.Set;

public class RitualsClient implements Rituals {

    private static final Map<ResourceLocation, RitualRenderer> RENDERERS = Maps.newHashMap();
    private static RitualsClient instance;
    private final Map<BlockPos, Ritual> rituals = Maps.newHashMap();

    static {
        registerRitualRenderer(SummoningCircle.ID, new SummoningCircleRenderer());
    }

    public static void registerRitualRenderer(ResourceLocation id, RitualRenderer renderer) {
        RENDERERS.put(id, renderer);
    }

    @Override
    public Ritual getRitual(BlockPos pos) {
        Ritual ritual = rituals.get(pos);
        if (ritual != null) return ritual;
        return findRitual(pos);
    }

    @Override
    public Ritual getRitual(double x, double y, double z, double range) {
        double rangeSqr = range * range;
        for (Ritual ritual : rituals.values()) if (ritual.getCenter().distanceSqToCenter(x, y, z) <= rangeSqr) return ritual;
        return null;
    }

    private Ritual findRitual(BlockPos pos) {
        for (Ritual ritual : rituals.values()) if (ritual.contains(pos)) return ritual;
        return null;
    }

    @Override
    public void addRitual(Ritual ritual) {
        if (ritual == null) return;
        rituals.put(ritual.getCenter(), ritual);
    }

    @Override
    public void removeRitual(BlockPos pos) {
        rituals.remove(pos);
    }

    @Override
    public void tick() {
        Set<BlockPos> toRemove = Sets.newHashSet();
        WorldClient world = Minecraft.getMinecraft().world;
        for (Ritual ritual : rituals.values()) {
            if (!world.isBlockLoaded(ritual.getCenter())) {
                toRemove.add(ritual.getCenter());
                continue;
            }
            ritual.tick(world);
        }
        for (BlockPos pos : toRemove) rituals.remove(pos);
    }

    public void clear() {
        rituals.clear();
    }

    public void renderRituals() {
        WorldClient world = Minecraft.getMinecraft().world;
        for (Ritual ritual : rituals.values()) {
            if (!world.isBlockLoaded(ritual.getCenter())) continue;
            renderRitual(ritual);
        }
    }

    public void renderRitual(Ritual ritual) {
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

    public void applyTransformations(Ritual ritual) {
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

    public static RitualsClient getInstance() {
        if (instance == null) instance = new RitualsClient();
        return instance;
    }

}
