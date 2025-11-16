package net.smileycorp.magiadaemonica.common.rituals;

import net.minecraft.util.EnumFacing;

public enum Rotation {

    NORTH(EnumFacing.NORTH, pattern -> pattern, pos -> pos),
    EAST(EnumFacing.EAST, pattern -> {
        int width = pattern.length;
        int height = pattern[0].length;
        int[][] rotated = new int[height][width];
        for (int x = 0; x < width; x++)
            for (int z = 0; z < height; z++)
                rotated[z][x] = pattern[x][z];
        return rotated;
    }, pos -> new float[] {-pos[1], pos[0]}),
    SOUTH(EnumFacing.SOUTH, pattern -> {
        int width = pattern.length;
        int height = pattern[0].length;
        int[][] rotated = new int[width][height];
        for (int x = 0; x < width; x++)
            for (int z = 0; z < height; z++)
                rotated[width - x - 1][height - z - 1] = pattern[x][z];
        return rotated;
    }, pos -> new float[] {-pos[0], -pos[1]}),
    WEST(EnumFacing.WEST, pattern -> {
        int width = pattern.length;
        int height = pattern[0].length;
        int[][] rotated = new int[height][width];
        for (int x = 0; x < width; x++)
            for (int z = 0; z < height; z++)
                rotated[height - z - 1][width - x - 1] = pattern[x][z];
        return rotated;
    }, pos -> new float[] {pos[1], -pos[0]});

    private final EnumFacing facing;
    private final PatternTransformer patternFunc;
    private final PosTransformer posFunc;

    Rotation(EnumFacing facing, PatternTransformer patternFunc, PosTransformer posFunc) {
        this.facing = facing;
        this.patternFunc = patternFunc;
        this.posFunc = posFunc;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public int[][] apply(int[][] pattern) {
        return patternFunc.apply(pattern);
    }

    public float[] apply(float[] pos) {
        return posFunc.apply(pos);
    }

}
