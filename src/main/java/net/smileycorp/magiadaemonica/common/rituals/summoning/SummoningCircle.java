package net.smileycorp.magiadaemonica.common.rituals.summoning;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.smileycorp.atlas.api.util.DirectionUtils;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.blocks.BlockChalkLine;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.common.rituals.Ritual;
import net.smileycorp.magiadaemonica.common.rituals.Rotation;

import java.util.Random;

public class SummoningCircle implements Ritual {

    public static final ResourceLocation ID = Constants.loc("summoning_circle");

    private final BlockPos pos;
    private final int width, height;
    private final Vec3d center;
    private final ResourceLocation name;
    private final float[][] candles;
    private boolean mirror;
    private Rotation rotation = Rotation.NORTH;
    private boolean isDirty;
    private int power, lastPower = 0;
    private boolean active = false;
    private int ticksActive;

    public SummoningCircle(ResourceLocation name, BlockPos pos, int width, int height) {
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.center = new Vec3d(pos.getX() + width  * 0.5f, pos.getY(), pos.getZ() + height  * 0.5f);
        this.name = name;
        this.candles = SummoningCircles.getCandles(name);
    }

    public void setBlocks(World world, BlockChalkLine.RitualState ritualState) {
        BlockPos.MutableBlockPos mutable;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                mutable = new BlockPos.MutableBlockPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
                IBlockState state = world.getBlockState(mutable);
                if (state.getBlock() != DaemonicaBlocks.CHALK_LINE) continue;
                world.setBlockState(mutable, state.withProperty(BlockChalkLine.RITUAL_STATE, ritualState));
            }
        }
    }

    @Override
    public void removeBlocks(World world) {
       setBlocks(world, BlockChalkLine.RitualState.NONE);
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void markDirty(boolean dirty) {
        isDirty = dirty;
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
    public Vec3d getCenter() {
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
    public void tick(World world) {
        if (world.isRemote) {
            lastPower = power;
            Random rand = world.rand;
            for (float[] candle : candles) {
                candle = rotation.apply(candle);
                if (mirror) for (int i = 0; i < 2; i++) candle[i] = -candle[i];
                IBlockState state = world.getBlockState(new BlockPos(center.addVector(candle[0], 0, candle[1])));
                if (state.getBlock() != DaemonicaBlocks.CHALK_LINE) continue;
                if (state.getValue(BlockChalkLine.CANDLE) != BlockChalkLine.Candle.LIT) continue;
                world.spawnParticle(EnumParticleTypes.FLAME, center.x + candle[0], center.y + 0.6, center.z + candle[1], 0, 0, 0);
                if (rand.nextInt(4) == 0)
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, center.x + candle[0] + (rand.nextFloat() - 0.5) * 0.05,
                            center.y + 0.6, center.z + candle[1] + (rand.nextFloat() - 0.5) * 0.05, 0, 0, 0);
            }
            if (ticksActive > 80 && ticksActive < 200) spawnParticles(world, EnumParticleTypes.SMOKE_NORMAL);
            if (ticksActive > 200) spawnParticles(world, EnumParticleTypes.FLAME);
            return;
        }
        if (!active) return;
        //if (ticksActive == 3) world.setBlockState(new BlockPos(center), Blocks.FIRE.getDefaultState());
        if (ticksActive == 80) {
            world.setBlockState(new BlockPos(center), Blocks.AIR.getDefaultState());
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
            for (float[] candle : candles) {
                candle = rotation.apply(candle);
                if (mirror) for (int i = 0; i < 2; i++) candle[i] = -candle[i];
                mutable.setPos(center.x + candle[0], center.y, center.z + candle[1]);
                world.setBlockState(mutable, world.getBlockState(mutable).withProperty(BlockChalkLine.CANDLE, BlockChalkLine.Candle.UNLIT));
            }
            world.playSound(null, center.x, center.y, center.z, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.HOSTILE, 0.75f, 1);
            for (EntityPlayerMP player : world.getPlayers(EntityPlayerMP.class, player -> player.getDistanceSq(center.x, center.y, center.z) <= 256))
                player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 140, 4, true, true));
        }
        if (ticksActive == 200) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
            for (float[] candle : candles) {
                candle = rotation.apply(candle);
                if (mirror) for (int i = 0; i < 2; i++) candle[i] = -candle[i];
                mutable.setPos(center.x + candle[0], center.y, center.z + candle[1]);
                world.setBlockState(mutable, world.getBlockState(mutable).withProperty(BlockChalkLine.CANDLE, BlockChalkLine.Candle.LIT));
            }
        }
        if (ticksActive > 200 && ticksActive % 40 == 0)
            world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE, 0.75f, 1);
        ticksActive++;
        isDirty = true;
    }

    private void spawnParticles(World world, EnumParticleTypes type) {
        Random rand = world.rand;
        int r = width/2;
        for (int i = 0; i < rand.nextInt(3); i++) {
            Vec3d dir = DirectionUtils.getRandomDirectionVecXZ(world.rand);
            world.spawnParticle(type, center.x + dir.x * r, center.y + 0.05, center.z + dir.z * r, 0, 0, 0);
        }
        for (int i = 0; i < rand.nextInt(3) + 2; i++) {
            Vec3d dir = DirectionUtils.getRandomDirectionVecXZ(world.rand);
            float magnitude = rand.nextFloat() * r;
            world.spawnParticle(type, center.x + dir.x * magnitude, center.y + 0.05, center.z + dir.z * magnitude, 0, 0.01 * (r - magnitude), 0);
        }
    }

    @Override
    public void addPower(int power) {
        this.power += power;
        if (this.power > 2000) this.power = 2000;
        isDirty = true;
    }

    @Override
    public int getPower() {
        return power;
    }

    public int getLastTickPower() {
        return lastPower;
    }

    public int getTicksActive() {
        return ticksActive;
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
        nbt.setBoolean("active", active);
        nbt.setInteger("power", power);
        nbt.setInteger("ticksActive", ticksActive);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        rotation = Rotation.values()[nbt.getByte("rotation")];
        mirror = nbt.getBoolean("mirror");
        active = nbt.getBoolean("active");
        power = nbt.getInteger("power");
        ticksActive = nbt.getInteger("ticksActive");
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean canPower() {
        return !active && power < 2000;
    }

    @Override
    public void processInvocation(EntityPlayer player, String invocation) {
        if (active || power < 500) return;
        World world = player.world;
        if (world.isDaytime()) return;
        if (!"te infernale invoco pacisci volo".equals(invocation)) return;
        for (float[] candle : candles) {
            candle = rotation.apply(candle);
            if (mirror) for (int i = 0; i < 2; i++) candle[i] = -candle[i];
            IBlockState state = world.getBlockState(new BlockPos(center.addVector(candle[0], 0, candle[1])));
            if (state.getBlock() != DaemonicaBlocks.CHALK_LINE) return;
            if (state.getValue(BlockChalkLine.CANDLE) != BlockChalkLine.Candle.LIT) return;
        }
        EntityLightningBolt bolt = new EntityLightningBolt(world, center.x, center.y, center.z, false);
        world.spawnEntity(bolt);
        setBlocks(world, BlockChalkLine.RitualState.ACTIVE);
        active = true;
    }

    public static SummoningCircle fromNBT(NBTTagCompound nbt) {
        SummoningCircle circle = new SummoningCircle(new ResourceLocation(nbt.getString("name")),
                NBTUtil.getPosFromTag(nbt.getCompoundTag("pos")), nbt.getInteger("width"), nbt.getInteger("height"));
        circle.readFromNBT(nbt);
        return circle;
    }

}
