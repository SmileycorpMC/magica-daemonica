package net.smileycorp.magiadaemonica.common.rituals;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface Ritual {

    ResourceLocation getID();

    BlockPos getPos();

    default BlockPos getCenterPos() {
        return new BlockPos(getCenter());
    }

    Vec3d getCenter();

    int getWidth();

    int getHeight();

    Rotation getRotation();

    boolean isMirrored();

    void tick(World world);

    void addPower(int power);

    int getPower();

    default boolean contains(BlockPos pos) {
        BlockPos origin = getPos();
        if (pos.getY() < origin.getY() || pos.getY() > origin.getY() + 5) return false;
        if (pos.getX() < origin.getX() || pos.getX() > origin.getX() + getWidth()) return false;
        return pos.getZ() >= origin.getZ() && pos.getZ() <= origin.getZ() + getHeight();
    }

    void removeBlocks(World world);

    boolean isDirty();

    void markDirty(boolean dirty);

    NBTTagCompound writeToNBT();

    void readFromNBT(NBTTagCompound nbt);

    boolean isActive();

    boolean canPower();

    void processInvocation(EntityPlayer player, String invocation);

}
