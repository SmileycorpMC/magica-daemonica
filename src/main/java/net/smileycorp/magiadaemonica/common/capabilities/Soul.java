package net.smileycorp.magiadaemonica.common.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;

public interface Soul {

    float getSoul();

    void consumeSoul(float percent, boolean flat);

    void setSoul(float percent);

    class Impl implements Soul {

        private float soul_percent = 1;

        @Override
        public float getSoul() {
            return soul_percent;
        }

        @Override
        public void consumeSoul(float percent, boolean flat) {
            setSoul(Math.max(0, flat ? soul_percent - percent : soul_percent * (1 - percent)));
        }

        @Override
        public void setSoul(float percent) {
            soul_percent = percent;
        }

    }

    class Storage implements Capability.IStorage<net.smileycorp.magiadaemonica.common.capabilities.Soul> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<net.smileycorp.magiadaemonica.common.capabilities.Soul> capability, net.smileycorp.magiadaemonica.common.capabilities.Soul soul, EnumFacing enumFacing) {
            return new NBTTagFloat(soul.getSoul());
        }

        @Override
        public void readNBT(Capability<net.smileycorp.magiadaemonica.common.capabilities.Soul> capability, net.smileycorp.magiadaemonica.common.capabilities.Soul soul, EnumFacing enumFacing, NBTBase nbtBase) {
            soul.setSoul(((NBTTagFloat)nbtBase).getFloat());
        }

    }

    class Provider implements ICapabilitySerializable<NBTBase> {

        protected net.smileycorp.magiadaemonica.common.capabilities.Soul instance = DaemonicaCapabilities.SOUL.getDefaultInstance();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == DaemonicaCapabilities.SOUL;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            return capability == DaemonicaCapabilities.SOUL ? DaemonicaCapabilities.SOUL.cast(instance) : null;
        }

        @Override
        public NBTBase serializeNBT() {
            return DaemonicaCapabilities.SOUL.getStorage().writeNBT(DaemonicaCapabilities.SOUL, instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            DaemonicaCapabilities.SOUL.getStorage().readNBT(DaemonicaCapabilities.SOUL, instance, null, nbt);
        }

    }

}
