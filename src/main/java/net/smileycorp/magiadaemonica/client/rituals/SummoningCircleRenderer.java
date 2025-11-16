package net.smileycorp.magiadaemonica.client.rituals;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
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
        ResourceLocation texture = new ResourceLocation(name.getResourceDomain(), "textures/summoning_circles/" + name.getResourcePath() + ".png");
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(texture);
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
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(0, 0.01, 0).tex(0, 0).normal(0, 1, 0).endVertex();
        buffer.pos(0, 0.01 , height).tex(0, 1).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0.01, height).tex(1, 1).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0.01, 0).tex(1, 0).normal(0, 1, 0).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        mc.getTextureManager().bindTexture(texture);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        int offsetX = width/2;
        int offsetZ = height/2;
        for (float[] candle : ritual.getCandles()) {
            renderCandle(mc, ritual, candle[0] + offsetX - 0.5, candle[1] + offsetZ - 0.5);
        }
    }

    private void renderCandle(Minecraft mc, IRitual ritual, double x, double z) {
        WorldClient world = mc.world;
        BlockPos pos = ritual.getCenter().add(x, 0, z);
        GlStateManager.pushMatrix();
        IBlockState state = world.getBlockState(pos);
        boolean lit = false;
        if (state.getBlock() == DaemonicaBlocks.CHALK_LINE) lit = state.getValue(BlockChalkLine.CANDLE) == BlockChalkLine.Candle.LIT;
        int i = world.getCombinedLight(pos, 0);
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        IBlockState state1 = DaemonicaBlocks.SCENTED_CANDLE.getDefaultState().withProperty(BlockScentedCandle.LIT, lit);
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        BlockPos origin = ritual.getPos();
        int width = ritual.getWidth();
        int height = ritual.getHeight();
        GlStateManager.translate(pos.getX() - TileEntityRendererDispatcher.staticPlayerX,
                pos.getY() - TileEntityRendererDispatcher.staticPlayerY,
                pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);
        GlStateManager.translate(x, 0, z);
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        dispatcher.getBlockModelRenderer().renderModel(world, dispatcher.getModelForState(state1), state1, BlockPos.ORIGIN, buffer, false, MathHelper.getPositionRandom(pos));
        tessellator.draw();
        GlStateManager.popMatrix();
    }


}
