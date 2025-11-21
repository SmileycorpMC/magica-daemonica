package net.smileycorp.magiadaemonica.client.rituals;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.smileycorp.magiadaemonica.client.rituals.renderers.RitualRenderer;
import net.smileycorp.magiadaemonica.client.rituals.renderers.SummoningCircleRenderer;
import net.smileycorp.magiadaemonica.common.rituals.Ritual;
import net.smileycorp.magiadaemonica.common.rituals.Rituals;
import net.smileycorp.magiadaemonica.common.rituals.RitualsRegistry;
import net.smileycorp.magiadaemonica.common.rituals.summoning.SummoningCircle;

import java.util.Collection;
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
        for (Ritual ritual : rituals.values()) if (ritual.getCenterPos().distanceSqToCenter(x, y, z) <= rangeSqr) return ritual;
        return null;
    }

    private Ritual findRitual(BlockPos pos) {
        for (Ritual ritual : rituals.values()) if (ritual.contains(pos)) return ritual;
        return null;
    }

    @Override
    public void addRitual(Ritual ritual) {
        if (ritual == null) return;
        rituals.put(ritual.getCenterPos(), ritual);
    }

    public void addRitual(BlockPos pos, NBTTagCompound nbt) {
        if (nbt == null) return;
        Ritual ritual = getRitual(pos);
        if (ritual != null) {
            ritual.readFromNBT(nbt);
            return;
        }
        ritual = RitualsRegistry.getRitualFromNBT(nbt);
        rituals.put(ritual.getCenterPos(), ritual);
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
            if (!world.isBlockLoaded(ritual.getCenterPos())) {
                toRemove.add(ritual.getCenterPos());
                continue;
            }
            ritual.tick(world);
        }
        for (BlockPos pos : toRemove) rituals.remove(pos);
    }

    @Override
    public Collection<Ritual> getRituals() {
        return rituals.values();
    }

    public void clear() {
        rituals.clear();
    }

    public void renderRituals() {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.world;
        float partialTicks = mc.getRenderPartialTicks();
        for (Ritual ritual : rituals.values()) {
            if (!world.isBlockLoaded(ritual.getCenterPos())) continue;
            RitualRenderer renderer = RENDERERS.get(ritual.getID());
            if (renderer == null) continue;
            int width = ritual.getWidth();
            int height = ritual.getHeight();
            BlockPos pos = ritual.getPos();
            GlStateManager.pushMatrix();
            applyTransformations(ritual);
            renderer.render(ritual, pos.getX() - TileEntityRendererDispatcher.staticPlayerX,
                    pos.getY() - TileEntityRendererDispatcher.staticPlayerY,
                    pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ, width, height, partialTicks);
            GlStateManager.popMatrix();
        }
    }

    public void applyTransformations(Ritual ritual) {
        Vec3d pos = ritual.getCenter();
        GlStateManager.translate(pos.x - TileEntityRendererDispatcher.staticPlayerX,
                pos.y - TileEntityRendererDispatcher.staticPlayerY,
                pos.z - TileEntityRendererDispatcher.staticPlayerZ);
        switch (ritual.getRotation()) {
            case EAST:
                GlStateManager.rotate(90, 0, 1, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(180, 0, 1, 0);
                break;
            case WEST:
                GlStateManager.rotate(-90, 0, 1, 0);
                break;
        }
    }

    public static RitualsClient getInstance() {
        if (instance == null) instance = new RitualsClient();
        return instance;
    }

}
