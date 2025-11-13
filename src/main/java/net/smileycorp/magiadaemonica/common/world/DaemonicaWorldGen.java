package net.smileycorp.magiadaemonica.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.smileycorp.atlas.api.config.WorldGenEntry;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.config.WorldConfig;

import java.util.Random;

public class DaemonicaWorldGen implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        genOre(DaemonicaBlocks.CHALK.getBase().getDefaultState(), WorldConfig.chalk, world, random, chunkX, chunkZ);
    }

    private void genOre(IBlockState block, WorldGenEntry entry, World world, Random rand, int chunkX, int chunkZ) {
        for (int dim : entry.getDimensions()) {
            if (world.provider.getDimension() != dim) continue;
            WorldGenSimpleOre generator = new WorldGenSimpleOre(entry.getSize(), block);
            int dy = entry.getMaxHeight() - entry.getMinHeight() + 1;
            if (dy < 1) return;
            for (int i = 0; i < entry.getSpawnChances(); i++) {
                int x = chunkX * 16 + rand.nextInt(16);
                int y = entry.getMinHeight() + rand.nextInt(dy);
                int z = chunkZ * 16 + rand.nextInt(16);
                generator.generate(world, rand, new BlockPos(x, y, z));
            }
        }
    }

}
