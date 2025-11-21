package net.smileycorp.magiadaemonica.common.rituals;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.magiadaemonica.common.rituals.summoning.SummoningCircle;

import java.util.Map;
import java.util.function.Function;

public class RitualsRegistry {

    private static final Map<ResourceLocation, Function<NBTTagCompound, Ritual>> RITUAL_TYPES = Maps.newHashMap();
    private static final Map<String, ResourceLocation> INVOCATIONS = Maps.newHashMap();

    static {
       registerRitualType(SummoningCircle.ID, SummoningCircle::fromNBT);
       registerInvocation("te infernale invoco pacisci volo", SummoningCircle.ID);
    }

    public static void registerRitualType(ResourceLocation id, Function<NBTTagCompound, Ritual> factory) {
        RITUAL_TYPES.put(id, factory);
    }

    public static void registerInvocation(String invocation, ResourceLocation ritualType) {
        INVOCATIONS.put(invocation, ritualType);
    }

    public static void processInvocation(EntityPlayer player, String invocation) {
        ResourceLocation ritualType = INVOCATIONS.get(invocation);
        if (ritualType == null) return;
        for (Ritual ritual : Rituals.get(player.world).getRituals()) {
            if (!ritual.getID().equals(ritualType)) continue;
            int disSqr = ((ritual.getWidth() / 2) + 2) * ((ritual.getHeight() / 2) + 2);
            if (ritual.getCenterPos().distanceSqToCenter(player.posX, player.posY, player.posZ) > disSqr) continue;
            ritual.processInvocation(player, invocation);
        }
    }

    public static Ritual getRitualFromNBT(NBTTagCompound nbt) {
        Function<NBTTagCompound, Ritual> factory = RITUAL_TYPES.get(new ResourceLocation(nbt.getString("id")));
        return factory == null ? null : factory.apply(nbt);
    }

}
