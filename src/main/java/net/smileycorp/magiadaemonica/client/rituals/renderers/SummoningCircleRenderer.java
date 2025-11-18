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
import net.smileycorp.magiadaemonica.common.rituals.summoning.SummoningCircle;

public class SummoningCircleRenderer implements RitualRenderer<SummoningCircle> {

    @Override
    public void render(SummoningCircle ritual, double x, double y, double z, int width, int height) {
        ResourceLocation name = ritual.getName();
        if (name == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        //setup lighting
        RenderHelper.enableStandardItemLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        //summoning circle
        int i = mc.world.getCombinedLight(ritual.getCenter(), 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(new ResourceLocation(name.getResourceDomain(), "textures/summoning_circles/" + name.getResourcePath() + ".png"));
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float r = 1 - Math.min(ritual.getPower(), 100) * 0.0032f;
        float gb = 1 - Math.min(ritual.getPower(), 100) * 0.01f;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        buffer.pos(0, 0.01, 0).tex(0, 0).color(r, gb, gb, 1).normal(0, 1, 0).endVertex();
        buffer.pos(0, 0.01 , height).tex(0, 1).color(r, gb, gb, 1).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0.01, height).tex(1, 1).color(r, gb, gb, 1).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0.01, 0).tex(1, 0).color(r, gb, gb, 1).normal(0, 1, 0).endVertex();
        tessellator.draw();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        //candles
        float offsetX = width * 0.5f - 0.5f;
        float offsetZ = height * 0.5f - 0.5f;
        WorldClient world = mc.world;
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
        GlStateManager.disableLighting();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        for (float[] candle : ritual.getCandles()) {
            BlockPos pos = ritual.getPos().add(x, 0, z);
            IBlockState state = world.getBlockState(pos);
            boolean lit = false;
            if (state.getBlock() == DaemonicaBlocks.CHALK_LINE) lit = state.getValue(BlockChalkLine.CANDLE) == BlockChalkLine.Candle.LIT;
            IBlockState renderState = DaemonicaBlocks.SCENTED_CANDLE.getDefaultState().withProperty(BlockScentedCandle.LIT, lit);
            buffer.setTranslation(candle[0] + offsetX - pos.getX(), -pos.getY(), candle[1] + offsetZ - pos.getZ());
            dispatcher.getBlockModelRenderer().renderModel(world, dispatcher.getModelForState(renderState), renderState,  pos, buffer, false, MathHelper.getPositionRandom(pos));
            buffer.setTranslation(0, 0, 0);
        }
        tessellator.draw();
        GlStateManager.enableLighting();
    }

}
