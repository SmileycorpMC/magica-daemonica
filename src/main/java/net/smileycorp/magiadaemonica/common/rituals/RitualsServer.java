package net.smileycorp.magiadaemonica.common.rituals;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.network.PacketHandler;
import net.smileycorp.magiadaemonica.common.network.RemoveRitualMessage;
import net.smileycorp.magiadaemonica.common.network.SyncRitualMessage;

import java.util.Collection;
import java.util.Map;

public class RitualsServer extends WorldSavedData implements Rituals {

    public static final String DATA = Constants.MODID + "_rituals";

    private final Map<BlockPos, Ritual> rituals = Maps.newHashMap();
    private WorldServer world;

    public RitualsServer(String data) {
        super(data);
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
        rituals.put(ritual.getCenterPos(), ritual);
        syncRitual(ritual);
        markDirty();
    }

    @Override
    public void removeRitual(BlockPos pos) {
        Ritual ritual = getRitual(pos);
        if (ritual == null) return;
        ritual.removeBlocks(world);
        pos = ritual.getCenterPos();
        rituals.remove(pos);
        PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new RemoveRitualMessage(pos),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(),
                        128));
        markDirty();
    }

    @Override
    public void tick() {
        for (Ritual ritual : rituals.values()) {
            ritual.tick(world);
            if (ritual.isDirty()) {
                markDirty();
                syncRitual(ritual);
            }
        }
    }

    @Override
    public Collection<Ritual> getRituals() {
        return rituals.values();
    }

    public void syncRitual(Ritual ritual) {
        BlockPos pos = ritual.getCenterPos();
        PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new SyncRitualMessage(pos, ritual),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
        ritual.markDirty(false);
    }

    public void syncRituals(Chunk chunk) {
        for (Ritual ritual : rituals.values()) {
            BlockPos pos = ritual.getCenterPos();
            if (world.getChunkFromBlockCoords(pos) == chunk) syncRitual(ritual);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey("rituals")) return;
        for (NBTBase tag : nbt.getTagList("rituals", 10)) {
            Ritual ritual = RitualsRegistry.getRitualFromNBT((NBTTagCompound) tag);
            if (ritual != null) rituals.put(ritual.getCenterPos(), ritual);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList rituals = new NBTTagList();
        for (Ritual ritual : this.rituals.values()) {
            NBTTagCompound tag = ritual.writeToNBT();
            tag.setString("id", ritual.getID().toString());
            rituals.appendTag(tag);
        }
        nbt.setTag("rituals", rituals);
        return nbt;
    }

    public static RitualsServer get(WorldServer world) {
        RitualsServer data = (RitualsServer) world.getMapStorage().getOrLoadData(RitualsServer.class, DATA);
        if (data == null) {
            data = new RitualsServer(DATA);
            world.getMapStorage().setData(DATA, data);
        }
        if (data.world == null) data.world = world;
        return data;
    }

}
