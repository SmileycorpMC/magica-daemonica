package net.smileycorp.magiadaemonica.client.rituals.renderers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.blocks.BlockChalkLine;
import net.smileycorp.magiadaemonica.common.blocks.BlockScentedCandle;
import net.smileycorp.magiadaemonica.common.blocks.DaemonicaBlocks;
import net.smileycorp.magiadaemonica.common.rituals.summoning.SummoningCircle;

public class SummoningCircleRenderer implements RitualRenderer<SummoningCircle> {

    private static final ResourceLocation SUMMONING_RUNES = Constants.loc("textures/summoning_circles/summoning_runes.png");

    @Override
    public void render(SummoningCircle ritual, float partialTicks) {
        ResourceLocation name = ritual.getName();
        if (name == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.getTextureManager();
        boolean hasLighting = ritual.getTicksActive() <= 200;
        float w = ritual.getWidth() * 0.5f;
        float h = ritual.getHeight() * 0.5f;
        //setup lighting
        if (hasLighting) {
            RenderHelper.enableStandardItemLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        } else {
            RenderHelper.disableStandardItemLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }
        GlStateManager.enableFog();
        //summoning circle
        float r;
        float g;
        float b;
        if (!hasLighting) {
            r = 1;
            g = 0.231f;
            b = 0;
        } else {
            float power = Math.min(ritual.getLastTickPower() + (ritual.getPower() - ritual.getLastTickPower()) * partialTicks, 2000) / 2000f;
            r = 1 - power * 0.35f;
            g = 1 - power;
            b = g;
            int i = mc.world.getCombinedLight(ritual.getCenterPos(), 0);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i % 65536, i / 65536);
        }
        GlStateManager.color(1, 1, 1, 1);
        textureManager.bindTexture(new ResourceLocation(name.getResourceDomain(), "textures/summoning_circles/" + name.getResourcePath() + ".png"));
        renderPlane(-w, 0.01, -h, w, 0.01, h, r, g, b, 1, false);
        //runes
        if (!hasLighting) {
            GlStateManager.enableBlend();
            textureManager.bindTexture(SUMMONING_RUNES);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            float t = ritual.getTicksActive() + partialTicks;
            GlStateManager.pushMatrix();
            GlStateManager.rotate(MathHelper.wrapDegrees(t * 18), 0, 1, 0);
            renderPlane(-w - 0.25, 0.5, -h -0.25, w + 0.25, 4, h + 0.25,  0.678f, 0, 0, 0.5f, true);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(MathHelper.wrapDegrees(-t * 20), 0, 1, 0);
            renderPlane(-w - 1, 0.5, -h -1, w + 1, 4, h + 1,  0.678f, 0, 0, 0.5f, true);
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
        }
        if (hasLighting) {
            RenderHelper.disableStandardItemLighting();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }
        //candles
        WorldClient world = mc.world;
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
        GlStateManager.disableLighting();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        for (float[] candle : ritual.getCandles()) {
            BlockPos pos = ritual.getCenterPos().add(candle[0], 0, candle[1]);
            IBlockState state = world.getBlockState(pos);
            boolean lit = false;
            if (state.getBlock() == DaemonicaBlocks.CHALK_LINE)
                lit = state.getValue(BlockChalkLine.CANDLE) == BlockChalkLine.Candle.LIT;
            IBlockState renderState = DaemonicaBlocks.SCENTED_CANDLE.getDefaultState().withProperty(BlockScentedCandle.LIT, lit);
            buffer.setTranslation(candle[0] - pos.getX() - 0.5f, -pos.getY(), candle[1] - pos.getZ() - 0.5f);
            dispatcher.getBlockModelRenderer().renderModel(world, dispatcher.getModelForState(renderState), renderState, pos, buffer, false, MathHelper.getPositionRandom(pos));
        }
        buffer.setTranslation(0, 0, 0);
        tessellator.draw();
        GlStateManager.disableFog();
        GlStateManager.enableLighting();
    }

}
