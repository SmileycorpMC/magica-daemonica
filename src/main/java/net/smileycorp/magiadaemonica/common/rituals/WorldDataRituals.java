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

import java.util.Map;

public class WorldDataRituals extends WorldSavedData {

    public static final String DATA = Constants.MODID + "_rituals";

    private final Map<BlockPos, IRitual> rituals = Maps.newHashMap();
    private WorldServer world;

    public WorldDataRituals(String data) {
        super(data);
    }

    public IRitual getRitual(BlockPos pos) {
        IRitual ritual = rituals.get(pos);
        if (ritual != null) return ritual;
        return findRitual(pos);
    }

    private IRitual findRitual(BlockPos pos) {
        for (IRitual ritual : rituals.values()) if (ritual.contains(pos)) return ritual;
        return null;
    }

    public void addRitual(IRitual ritual) {
        rituals.put(ritual.getPos(), ritual);
        updateRitual(ritual);
        markDirty();
    }

    public void removeRitual(BlockPos pos) {
        IRitual ritual = getRitual(pos);
        if (ritual == null) return;
        ritual.removeBlocks(world);
        pos = ritual.getPos();
        rituals.remove(pos);
        PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new RemoveRitualMessage(pos),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(),
                        128));
        markDirty();
    }
    
    public void updateRitual(IRitual ritual) {
        BlockPos pos = ritual.getPos();
        PacketHandler.NETWORK_INSTANCE.sendToAllTracking(new SyncRitualMessage(ritual),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
    }

    public void syncRituals(Chunk chunk) {
        for (IRitual ritual : rituals.values()) {
            BlockPos pos = ritual.getPos();
            if (world.getChunkFromBlockCoords(pos) == chunk
                    || world.getChunkFromBlockCoords(pos.add(ritual.getWidth(), 0, ritual.getHeight())) == chunk)
                updateRitual(ritual);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey("rituals")) return;
        for (NBTBase tag : nbt.getTagList("rituals", 10)) {
            IRitual ritual = RitualsRegistry.getRitualFromNBT((NBTTagCompound) tag);
            if (ritual != null) rituals.put(ritual.getPos(), ritual);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList rituals = new NBTTagList();
        for (IRitual ritual : this.rituals.values()) {
            NBTTagCompound tag = ritual.writeToNBT();
            tag.setString("id", ritual.getID().toString());
            rituals.appendTag(tag);
        }
        nbt.setTag("rituals", rituals);
        return nbt;
    }

    public static WorldDataRituals get(WorldServer world) {
        WorldDataRituals data = (WorldDataRituals) world.getMapStorage().getOrLoadData(WorldDataRituals.class, DATA);
        if (data == null) {
            data = new WorldDataRituals(DATA);
            world.getMapStorage().setData(DATA, data);
        }
        if (data.world == null) data.world = world;
        return data;
    }

}
