package net.smileycorp.magiadaemonica.common.rituals.summoningcircle;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.smileycorp.magiadaemonica.common.blocks.BlockChalkLine;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.common.rituals.Rotation;

import java.util.List;

public class SummoningCirclePattern {

    private final ResourceLocation name;
    private final int width, height;
    private final int[][] pattern;
    private final boolean rotate, mirror;

    /*summoning circle patten matcher
            0 - air
            1 - chalk
            2 - candle
         */
    public SummoningCirclePattern(ResourceLocation name, boolean rotate, boolean mirror, int[][] pattern) {
        this.name = name;
        int width = 0;
        for (int[] column : pattern) if (width < column.length) width = column.length;
        this.rotate = rotate;
        this.mirror = mirror;
        this.height = pattern.length;
        this.width = width;
        this.pattern = new int[height][width];
        for (int i = 0; i < pattern.length; i++) System.arraycopy(pattern[i], 0, this.pattern[i], 0, pattern[i].length);
    }

    public ResourceLocation getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    //called when a candle is placed in chalk to check if the candle placement matches this pattern
    public SummoningCircle matches(World world, BlockPos pos) {
        if (rotate) for (Rotation rotation : Rotation.values()) {
            int[][] rotated = rotation.apply(pattern);
            SummoningCircle circle = checkMatch(world, pos, rotated);
            if (circle != null) {
                circle.setRotation(rotation);
                return circle;
            }
            if (mirror) {
                circle = checkMatch(world, pos, mirror(rotated));
                if (circle != null) {
                    circle.setRotation(rotation);
                    circle.mirror();
                    return circle;
                }
            }
        }
        else {
            SummoningCircle circle = checkMatch(world, pos, pattern);
            if (circle != null) return circle;
            if (mirror) {
                circle = checkMatch(world, pos, mirror(pattern));
                if (circle != null) {
                    circle.mirror();
                    return circle;
                }
            }
        }
        return null;
    }

    private SummoningCircle checkMatch(World world, BlockPos pos, int[][] pattern) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
        //check the candles first
        List<Tuple<Integer, Integer>> candles = getCandles(pattern);
        for (Tuple<Integer, Integer> candle : candles) {
            if (matchCandles(world, pos, mutable, candles, candle)) {
                //if the candles match we know the position of the original candle we checked
                //we can use it's position in the pattern to get the top left of the circle area
                pos = new BlockPos(pos.getX() - candle.getFirst(), pos.getY(), pos.getZ() - candle.getSecond());
                for (int z = 0; z < pattern.length; z++) {
                    for (int x = 0; x < pattern[0].length; x++) {
                        int i = pattern[z][x];
                        mutable.setPos(pos.getX() + x, pos.getY(), pos.getZ() + z);
                        if (i == 0) continue;
                        IBlockState state = world.getBlockState(mutable);
                        if (state.getBlock() != DaemonicaBlocks.CHALK_LINE) return null;
                        if (i == 1) {
                            if (state.getValue(BlockChalkLine.CANDLE) != BlockChalkLine.Candle.NONE) return null;
                            continue;
                        }
                        if (i == 2 && state.getValue(BlockChalkLine.CANDLE) == BlockChalkLine.Candle.NONE) return null;
                    }
                }
                return new SummoningCircle(pos, pattern.length, pattern[0].length, name);
            }
        }
        return null;
    }

    private boolean matchCandles(World world, BlockPos pos, BlockPos.MutableBlockPos mutable, List<Tuple<Integer, Integer>> candles, Tuple<Integer, Integer> candle) {
        for (Tuple<Integer, Integer> candle1: candles) {
            if (candle == candle1) continue;
            mutable.setPos(pos.getX() + candle1.getFirst() - candle.getFirst(), pos.getY(),
                    pos.getZ() + candle1.getSecond() - candle.getSecond());
            IBlockState state = world.getBlockState(mutable);
            if (state.getBlock() != DaemonicaBlocks.CHALK_LINE) return false;
            if (state.getValue(BlockChalkLine.CANDLE) == BlockChalkLine.Candle.NONE) return false;
        }
        return true;
    }

    private List<Tuple<Integer, Integer>> getCandles(int[][] pattern) {
        List<Tuple<Integer, Integer>> candles = Lists.newArrayList();
        for (int z = 0; z < pattern.length; z++)
            for (int x = 0; x < pattern[0].length; x++)
                if (pattern[z][x] == 2) candles.add(new Tuple<>(x, z));
        return candles;
    }

    private int[][] mirror(int[][] pattern) {
        int[][] mirror = new int[pattern.length][pattern[0].length];
        for (int i = 0; i < pattern.length; i++) System.arraycopy(pattern[i], 0, mirror[mirror.length - i - 1], 0, pattern[i].length);
        return mirror;
    }

}
