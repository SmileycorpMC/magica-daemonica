package net.smileycorp.magiadaemonica.common.rituals.summoningcircle;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.blocks.BlockChalkLine;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.common.rituals.IRitual;
import net.smileycorp.magiadaemonica.common.rituals.Rotation;

import java.util.Random;

public class SummoningCircle implements IRitual {

    public static final ResourceLocation ID = Constants.loc("summoning_circle");

    private final BlockPos pos;
    private final int width, height;
    private final Vec3d center;
    private final ResourceLocation name;
    private final float[][] candles;
    private boolean mirror;
    private Rotation rotation = Rotation.NORTH;

    public SummoningCircle(ResourceLocation name, BlockPos pos, int width, int height) {
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.center = new Vec3d(pos.getX() + width  * 0.5f, pos.getY(), pos.getZ() + height  * 0.5f);
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
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    public ResourceLocation getName() {
        return name;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public BlockPos getCenter() {
        return new BlockPos(center);
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
            candle = rotation.apply(candle);
            if (mirror) for (int i = 0; i < 2; i++) candle[i] = -candle[i];
            IBlockState state = world.getBlockState( new BlockPos(center.addVector(candle[0], 0, candle[1])));
            if (state.getBlock() != DaemonicaBlocks.CHALK_LINE) continue;
            if (state.getValue(BlockChalkLine.CANDLE) != BlockChalkLine.Candle.LIT) continue;
            world.spawnParticle(EnumParticleTypes.FLAME, center.x + candle[0], center.y + 0.6, center.z + candle[1], 0, 0, 0);
            if (rand.nextInt(4) == 0) world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, center.x + candle[0] + (rand.nextFloat() - 0.5) * 0.05,
                    center.y + 0.6,  center.z + candle[1] + (rand.nextFloat() - 0.5) * 0.05, 0, 0, 0);
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
        SummoningCircle circle = new SummoningCircle(new ResourceLocation(nbt.getString("name")),
                NBTUtil.getPosFromTag(nbt.getCompoundTag("pos")), nbt.getInteger("width"), nbt.getInteger("height"));
        circle.setRotation(Rotation.values()[nbt.getByte("rotation")]);
        if (nbt.getBoolean("mirror")) circle.mirror();
        return circle;
    }

}
