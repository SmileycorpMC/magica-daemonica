package net.smileycorp.magiadaemonica.common.rituals;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRitual {

    ResourceLocation getID();

    BlockPos getPos();

    int getWidth();

    int getHeight();

    EnumFacing getFacing();

    boolean isMirrored();

    default boolean contains(BlockPos pos) {
        BlockPos origin = getPos();
        if (pos.getY() < origin.getY() || pos.getY() > origin.getY() + 5) return false;
        if (pos.getX() < origin.getX() || pos.getX() > origin.getX() + getWidth()) return false;
        return pos.getZ() >= origin.getZ() && pos.getZ() <= origin.getZ() + getHeight();
    }

    void removeBlocks(World world);

    NBTTagCompound writeToNBT();

}
