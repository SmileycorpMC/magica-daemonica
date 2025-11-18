package net.smileycorp.magiadaemonica.common.rituals;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.magiadaemonica.common.rituals.summoning.SummoningCircle;

import java.util.Map;
import java.util.function.Function;

public class RitualsRegistry {

    private static final Map<ResourceLocation, Function<NBTTagCompound, Ritual>> RITUAL_TYPES = Maps.newHashMap();

    static {
       registerRitualType(SummoningCircle.ID, SummoningCircle::fromNBT);
    }

    public static void registerRitualType(ResourceLocation id, Function<NBTTagCompound, Ritual> factory) {
        RITUAL_TYPES.put(id, factory);
    }

    public static Ritual getRitualFromNBT(NBTTagCompound nbt) {
        Function<NBTTagCompound, Ritual> factory = RITUAL_TYPES.get(new ResourceLocation(nbt.getString("id")));
        return factory == null ? null : factory.apply(nbt);
    }

}
