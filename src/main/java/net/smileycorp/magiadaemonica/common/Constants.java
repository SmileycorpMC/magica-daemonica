package net.smileycorp.magiadaemonica.common;

import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public class Constants {
	
	public static final String NAME = "Magia Daemonica";
	public static final String MODID = "magiadaemonica";
	public static final String VERSION = "0.1.0";
	public static final String DEPENDENCIES = "required-after:atlaslib@[1.1.9,)";
	public static final String PATH = "net.smileycorp.magiadaemonica.";
	public static final String CLIENT = PATH + "client.ClientProxy";
	public static final String SERVER = PATH + "common.CommonProxy";

	public static String name(String name) {
		return MODID + "." + name.toLowerCase(Locale.US);
	}

	public static ResourceLocation loc(String name) {
		return new ResourceLocation(MODID, name.toLowerCase());
	}

	public static String locStr(String name) {
		return loc(name).toString();
	}

}
