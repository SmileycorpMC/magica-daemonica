package net.smileycorp.magiadaemonica.common.blocks;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.smileycorp.atlas.api.block.BlockBase;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.MagiaDaemonica;
import net.smileycorp.magiadaemonica.common.items.DaemonicaItems;
import net.smileycorp.magiadaemonica.common.rituals.WorldDataRituals;
import net.smileycorp.magiadaemonica.common.rituals.summoningcircle.SummoningCircles;

import java.util.Random;

public class BlockChalkLine extends BlockBase implements ILightable {

    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyEnum<Candle> CANDLE = PropertyEnum.create("candle", Candle.class);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    private final AxisAlignedBB AABB = new AxisAlignedBB(0, 0.0D, 0, 1, 0.0625D, 1);

    public BlockChalkLine() {
        super("chalk_line", Constants.MODID, Material.CIRCUITS, SoundType.STONE, 0, 0, 0, MagiaDaemonica.CREATIVE_TAB);
        setDefaultState(blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false)
                .withProperty(WEST, false).withProperty(CANDLE, Candle.NONE).withProperty(ACTIVE, false));
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, CANDLE, ACTIVE);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult raytrace, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(DaemonicaItems.CHALK_STICK);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) return;
        if (state.getValue(ACTIVE)) WorldDataRituals.get((WorldServer) world).removeRitual(pos);
        if (state.getValue(CANDLE) == Candle.NONE) return;
        EntityItem entityitem = new EntityItem(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f,
                new ItemStack(DaemonicaBlocks.SCENTED_CANDLE));
        entityitem.setDefaultPickupDelay();
        world.spawnEntity(entityitem);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) return false;
        if (state.getValue(ACTIVE)) return false;
        if (state.getValue(CANDLE) != Candle.NONE) return false;
        if (stack.getItem() != Item.getItemFromBlock(DaemonicaBlocks.SCENTED_CANDLE)) return false;
        if (!world.isRemote) {
            world.setBlockState(pos, state.withProperty(CANDLE, Candle.UNLIT));
            if (player instanceof EntityPlayerMP) CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
            SoundType sound = DaemonicaBlocks.SCENTED_CANDLE.getSoundType();
            world.playSound(null, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, sound.getPlaceSound(),
                    SoundCategory.BLOCKS, (sound.getVolume() + 1f) / 2f, sound.getPitch() * 0.8F);
            player.swingArm(hand);
            if (!player.isCreative()) stack.shrink(1);
            SummoningCircles.tryPlace(world, pos);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(CANDLE) == Candle.NONE ? AABB : FULL_BLOCK_AABB;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    /*@Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return state.getValue(ACTIVE) ? EnumBlockRenderType.INVISIBLE : super.getRenderType(state);
    }*/

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
        if (!world.isSideSolid(pos.down(), EnumFacing.UP, false) && world instanceof World) ((World) world).setBlockToAir(pos);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CANDLE, Candle.get(meta)).withProperty(ACTIVE, meta >= 3);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CANDLE).ordinal() + (state.getValue(ACTIVE) ? 3 : 0);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (world.getBlockState(pos.north()).getBlock() == this) state = state.withProperty(NORTH, true);
        if (world.getBlockState(pos.south()).getBlock() == this) state = state.withProperty(SOUTH, true);
        if (world.getBlockState(pos.east()).getBlock() == this) state = state.withProperty(EAST, true);
        if (world.getBlockState(pos.west()).getBlock() == this) state = state.withProperty(WEST, true);
        return state;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(CANDLE) == Candle.LIT ? 4 : 0;
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(CANDLE) != Candle.LIT) return;
        world.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 0, 0, 0);
        if (rand.nextInt(4) == 0) world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5 + (rand.nextFloat() - 0.5) * 0.05,
                pos.getY() + 0.6, pos.getZ() + 0.5 + (rand.nextFloat() - 0.5) * 0.05, 0, 0, 0);
    }

    @Override
    public boolean isLightable(World world, BlockPos pos, IBlockState state) {
        return state.getValue(CANDLE) == Candle.UNLIT;
    }

    @Override
    public void light(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state.withProperty(CANDLE, Candle.LIT));
    }

    public enum Candle implements IStringSerializable {

        NONE("none"),
        UNLIT("unlit"),
        LIT("lit");

        private final String name;

        Candle(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public static Candle get(int meta) {
            return values()[meta % values().length];
        }

    }

}
