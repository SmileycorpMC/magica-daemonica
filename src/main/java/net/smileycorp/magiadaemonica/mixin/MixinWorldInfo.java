package net.smileycorp.magiadaemonica.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.storage.WorldInfo;
import net.smileycorp.magiadaemonica.common.MagiaDaemonicaCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldInfo.class)
public class MixinWorldInfo {

    @Inject(at = @At("HEAD"), method = "isHardcoreModeEnabled")
    public void magicadaemonica$isHardcoreModeEnabled(CallbackInfoReturnable<Boolean> callback) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        if (!player.hasCapability(MagiaDaemonicaCapabilities.SOUL, null));
        if (player.getCapability(MagiaDaemonicaCapabilities.SOUL, null).getSoul() > 0) return;
        callback.setReturnValue(true);
    }

}
