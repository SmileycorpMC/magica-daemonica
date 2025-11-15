package net.smileycorp.magiadaemonica.common.rituals.summoningcircle;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.blocks.BlockChalkLine;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.common.rituals.IRitual;
import net.smileycorp.magiadaemonica.common.rituals.Rotation;

public class SummoningCircle implements IRitual {

    public static final ResourceLocation ID = Constants.loc("summoning_circle");

    private final BlockPos pos;
    private final int width, height;
    private final ResourceLocation name;
    private boolean mirror;
    private EnumFacing facing = EnumFacing.NORTH;

    public SummoningCircle(BlockPos pos, int width, int height, ResourceLocation name) {
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.name = name;
    }

    public void setBlocks(World world) {
        BlockPos.MutableBlockPos mutable;
        boolean addedRenderer = false;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                mutable = new BlockPos.MutableBlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
                IBlockState state = world.getBlockState(mutable);
                if (state.getBlock() != DaemonicaBlocks.CHALK_LINE) continue;
                world.setBlockState(mutable, state.withProperty(BlockChalkLine.ACTIVE, true));
            }
        }
    }

    @Override
    public void removeBlocks(World world) {
        BlockPos.MutableBlockPos mutable;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                mutable = new BlockPos.MutableBlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
                IBlockState state = world.getBlockState(mutable);
                if (state.getBlock() != DaemonicaBlocks.CHALK_LINE) continue;
                world.setBlockState(mutable, state.withProperty(BlockChalkLine.ACTIVE, false));
            }
        }
    }

    public void mirror() {
        this.mirror = true;
    }

    public void setFacing(Rotation rotation) {
        this.setFacing(rotation.getFacing());
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public ResourceLocation getName() {
        return name;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public boolean isMirrored() {
        return mirror;
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("pos", NBTUtil.createPosTag(pos));
        nbt.setInteger("width", width);
        nbt.setInteger("height", height);
        nbt.setString("name", name.toString());
        nbt.setByte("facing", (byte) facing.ordinal());
        nbt.setBoolean("mirror", mirror);
        return nbt;
    }

    public static SummoningCircle fromNBT(NBTTagCompound nbt) {
        SummoningCircle circle = new SummoningCircle(NBTUtil.getPosFromTag(nbt.getCompoundTag("pos")),
                nbt.getInteger("width"), nbt.getInteger("height"), new ResourceLocation(nbt.getString("name")));
        circle.setFacing(EnumFacing.values()[nbt.getByte("facing")]);
        if (nbt.getBoolean("mirror")) circle.mirror();
        return circle;
    }

}
