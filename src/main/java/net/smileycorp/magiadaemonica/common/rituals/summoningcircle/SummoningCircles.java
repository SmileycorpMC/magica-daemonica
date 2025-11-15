package net.smileycorp.magiadaemonica.common.rituals.summoningcircle;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.rituals.WorldDataRituals;

import java.util.Map;

public class SummoningCircles {

    private static Map<ResourceLocation, SummoningCirclePattern> PATTERNS = Maps.newHashMap();

    public static SummoningCirclePattern FIVE_POINT_PENTACLE = register(Constants.loc("five_point_pentacle"), true, false, new int[][]{
                    {0, 0, 1, 2, 1, 0, 0},
                    {0, 1, 0, 1, 0, 1, 0},
                    {2, 1, 1, 1, 1, 1, 2},
                    {1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 1, 1, 0, 1},
                    {0, 2, 1, 0, 1, 2, 0},
                    {0, 0, 1, 1, 1, 0, 0}});

    private static SummoningCirclePattern register(ResourceLocation name, boolean rotate, boolean mirror, int[][] pattern) {
        SummoningCirclePattern circle = new SummoningCirclePattern(name, rotate, mirror, pattern);
        PATTERNS.put(name, circle);
        return circle;
    }

    public static SummoningCircle findMatch(World world, BlockPos pos) {
        for (SummoningCirclePattern pattern : PATTERNS.values()) {
            SummoningCircle circle = pattern.matches(world, pos);
            if (circle != null) return circle;
        }
        return null;
    }

    public static void tryPlace(World world, BlockPos pos) {
        SummoningCircle circle = findMatch(world, pos);
        if (circle == null) return;
        circle.setBlocks(world);
        WorldDataRituals.get((WorldServer) world).addRitual(circle);
    }

}
