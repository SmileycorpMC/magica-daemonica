package net.smileycorp.magiadaemonica.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Lightable {

    boolean isLightable(World world, BlockPos pos, IBlockState state);

    void light(World world, BlockPos pos, IBlockState state);

}
