package net.smileycorp.magiadaemonica.client.rituals.renderers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.magiadaemonica.common.blocks.BlockChalkLine;
import net.smileycorp.magiadaemonica.common.blocks.BlockScentedCandle;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.common.rituals.IRitual;
import net.smileycorp.magiadaemonica.common.rituals.summoningcircle.SummoningCircle;

public class SummoningCircleRenderer implements RitualRenderer<SummoningCircle> {

    @Override
    public void render(SummoningCircle ritual, double x, double y, double z, int width, int height) {
        ResourceLocation name = ritual.getName();
        if (name == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(new ResourceLocation(name.getResourceDomain(), "textures/summoning_circles/" + name.getResourcePath() + ".png"));
        RenderHelper.enableStandardItemLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        int i = mc.world.getCombinedLight(ritual.getCenter(), 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        GlStateManager.color(1, 1, 1, 1);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        buffer.pos(0, 0.01, 0).tex(0, 0).color(255, 255, 255, 255).normal(0, 1, 0).endVertex();
        buffer.pos(0, 0.01 , height).tex(0, 1).color(255, 255, 255, 255).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0.01, height).tex(1, 1).color(255, 255, 255, 255).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0.01, 0).tex(1, 0).color(255, 255, 255, 255).normal(0, 1, 0).endVertex();
        tessellator.draw();
        float offsetX = width * 0.5f;
        float offsetZ = height * 0.5f;
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        for (float[] candle : ritual.getCandles()) renderCandle(mc, ritual, candle[0] + offsetX - 0.5, candle[1] + offsetZ - 0.5);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private void renderCandle(Minecraft mc, IRitual ritual, double x, double z) {
        WorldClient world = mc.world;
        BlockPos pos = ritual.getPos().add(x, 0, z);
        IBlockState state = world.getBlockState(pos);
        boolean lit = false;
        if (state.getBlock() == DaemonicaBlocks.CHALK_LINE) lit = state.getValue(BlockChalkLine.CANDLE) == BlockChalkLine.Candle.LIT;
        int i = world.getCombinedLight(pos, 0);
        int j = i % 65536;
        int k = i / 65536;
        GlStateManager.pushMatrix();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        IBlockState state1 = DaemonicaBlocks.SCENTED_CANDLE.getDefaultState().withProperty(BlockScentedCandle.LIT, lit);
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        GlStateManager.translate(x, 0 , z);
        dispatcher.getBlockModelRenderer().renderModel(world, dispatcher.getModelForState(state1), state1, BlockPos.ORIGIN, buffer, false, MathHelper.getPositionRandom(pos));
        tessellator.draw();
        GlStateManager.popMatrix();
    }


}
