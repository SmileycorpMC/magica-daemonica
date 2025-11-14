package net.smileycorp.magiadaemonica.mixin;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.smileycorp.magiadaemonica.common.blocks.BlockScentedCandle;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeEventFactory.class, remap = false)
public class MixinForgeEventFactory {

    /*we have to do this because the forge devs did not cook
    //they threw this shit raw out into the public
    for some ungodly reason this hook method has the place direction
    but they don't actually pass it to the fucking event?????
    the only solutions are this bullshit or some vec math shit/a ray trace
    and honestly considering that's already been done to call this method I'm not doing that shit
    */
    @Inject(at = @At(value = "TAIL"), method = "onPlayerBlockPlace")
    private static void magicadaemonica$onPlayerBlockPlaced(EntityPlayer player, BlockSnapshot snapshot, EnumFacing direction, EnumHand hand, CallbackInfoReturnable<BlockEvent.PlaceEvent> callback) {
        BlockEvent.PlaceEvent event = callback.getReturnValue();
        IBlockState state = event.getPlacedAgainst();
        World world = event.getWorld();
        if (event.getPlacedBlock().getBlock() != Blocks.FIRE) return;
        if (state.getBlock() != DaemonicaBlocks.SCENTED_CANDLE) return;
        if (state.getValue(BlockScentedCandle.LIT)) return;
        if (!event.getWorld().isRemote) world.setBlockState(event.getPos().offset(direction.getOpposite()), state.withProperty(BlockScentedCandle.LIT, true));
        event.setCanceled(true);
    }

}
