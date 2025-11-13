package net.smileycorp.magiadaemonica.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.smileycorp.atlas.api.config.WorldGenEntry;

import java.io.File;

public class WorldConfig {

    public static WorldGenEntry chalk;

    public static void syncConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getPath() + "/magiadaemonica/world.cfg"));
        try{
            config.load();
            chalk = new WorldGenEntry(config, "chalk", 33, 2, 55, 75, new int[]{0});
        } catch(Exception e) {
        } finally {
            if (config.hasChanged()) config.save();
        }
    }


}
