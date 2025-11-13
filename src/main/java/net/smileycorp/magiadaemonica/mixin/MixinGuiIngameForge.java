package net.smileycorp.magiadaemonica.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.GuiIngameForge;
import net.smileycorp.magiadaemonica.common.capabilities.DaemonicaCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GuiIngameForge.class, remap = false)
public class MixinGuiIngameForge {

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/WorldInfo;isHardcoreModeEnabled()Z", remap = true), method = "renderHealth")
    public boolean magicadaemonica$isHardcoreModeEnabled(WorldInfo instance, Operation<Boolean> original) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return false;
        if (player.world == null) return false;
        if (!player.world.isRemote) return false;
        if (!player.hasCapability(DaemonicaCapabilities.SOUL, null)) return false;
        return player.getCapability(DaemonicaCapabilities.SOUL, null).getSoul() <= 0;
    }

}
