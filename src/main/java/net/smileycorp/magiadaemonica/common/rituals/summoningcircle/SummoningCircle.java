package net.smileycorp.magiadaemonica.common.rituals.summoningcircle;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.blocks.BlockChalkLine;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.common.rituals.IRitual;
import net.smileycorp.magiadaemonica.common.rituals.Rotation;

import java.util.Random;

public class SummoningCircle implements IRitual {

    public static final ResourceLocation ID = Constants.loc("summoning_circle");

    private final BlockPos pos, center;
    private final int width, height;
    private final ResourceLocation name;
    private final float[][] candles;
    private boolean mirror;
    private Rotation rotation = Rotation.NORTH;

    public SummoningCircle(BlockPos pos, int width, int height, ResourceLocation name) {
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.center = pos.add(width/2, 0, height/2);
        this.name = name;
        this.candles = SummoningCircles.getCandles(name);
    }

    public void setBlocks(World world) {
        BlockPos.MutableBlockPos mutable;
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
        for (int i = 0; i < candles.length; i++) for (int j = 0; j < 2; j++) candles[i][j] = -candles[i][j];
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
        for (int i = 0; i < candles.length; i++) candles[i] = rotation.apply(candles[i]);
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
    public BlockPos getCenter() {
        return center;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public float[][] getCandles() {
        return candles;
    }

    @Override
    public Rotation getRotation() {
        return rotation;
    }

    @Override
    public boolean isMirrored() {
        return mirror;
    }

    @Override
    public void clientTick(World world) {
        Random rand = world.rand;
        for (float[] candle : candles) {
            world.spawnParticle(EnumParticleTypes.FLAME, center.getX() + candle[0], center.getY() + 0.6, center.getZ() + candle[1], 0, 0, 0);
            if (rand.nextInt(4) == 0) world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + candle[0] + (rand.nextFloat() - 0.5) * 0.05,
                    pos.getY() + 0.6,  + candle[1] + (rand.nextFloat() - 0.5) * 0.05, 0, 0, 0);
        }
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("pos", NBTUtil.createPosTag(pos));
        nbt.setInteger("width", width);
        nbt.setInteger("height", height);
        nbt.setString("name", name.toString());
        nbt.setByte("rotation", (byte) rotation.ordinal());
        nbt.setBoolean("mirror", mirror);
        return nbt;
    }

    public static SummoningCircle fromNBT(NBTTagCompound nbt) {
        SummoningCircle circle = new SummoningCircle(NBTUtil.getPosFromTag(nbt.getCompoundTag("pos")),
                nbt.getInteger("width"), nbt.getInteger("height"), new ResourceLocation(nbt.getString("name")));
        circle.setRotation(Rotation.values()[nbt.getByte("rotation")]);
        circle.mirror = nbt.getBoolean("mirror");
        return circle;
    }

}
