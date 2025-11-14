package net.smileycorp.magiadaemonica.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.atlas.api.config.BiomeGenEntry;
import net.smileycorp.atlas.api.config.WorldGenEntry;

import java.io.File;

public class WorldConfig {

    public static WorldGenEntry chalk;
    public static BiomeGenEntry chalkBiomes;
    public static int[] lavenderDimensions;
    public static int lavenderSpawnChance;
    public static int lavenderMinSize;
    public static int lavenderMaxSize;
    public static BiomeGenEntry lavenderBiomes;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/magiadaemonica/world.cfg"));
        try{
            config.load();
            chalk = new WorldGenEntry(config, "chalk", 33, 2, 55, 75, new int[]{0});
            chalkBiomes = new BiomeGenEntry(config, "chalk", new String[]{"BEACH", "OCEAN", "PLAINS", "FOREST"}, new String[0]);
            lavenderDimensions = config.get("lavender", "dimensions", new int[]{0}, "Which dimensions can lavender generate in?").getIntList();
            lavenderSpawnChance = config.get("lavender", "spawnChances", 20, "Chance for lavender to spawn per chunk (Set to 0 to disable generation)").getInt();
            lavenderMinSize = config.get("lavender", "minSize", 3, "Minimum amount of lavender to spawn per patch").getInt();
            lavenderMaxSize = config.get("lavender", "maxSize", 7, "Maximum amount of lavender to spawn per patch.").getInt();
            lavenderBiomes = new BiomeGenEntry(config, "chalk", new String[]{"PLAINS", "FOREST"}, new String[0]);
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }


}
