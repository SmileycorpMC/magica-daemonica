package net.smileycorp.magiadaemonica.client.tesr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.magiadaemonica.common.blocks.tiles.TileSummoningCircle;

public class TESRSummoningCircle extends TileEntitySpecialRenderer<TileSummoningCircle> {

    @Override
    public void render(TileSummoningCircle te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ResourceLocation circle = te.getCircle();
        if (circle == null) return;
        ResourceLocation texture = new ResourceLocation(circle.getResourceDomain(), "textures/summoning_circles/" + circle.getResourcePath() + ".png");
        GlStateManager.pushMatrix();
        BlockPos origin = te.getOrigin();
        BlockPos pos = te.getPos();
        int width = te.getWidth();
        int height = te.getHeight();
        GlStateManager.translate(x + (origin.getX() - pos.getX()), y + 0.02, z + (origin.getZ() - pos.getZ()));
        switch (te.getFacing()) {
            case EAST:
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.translate(-width, 0, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(180, 0, 1, 0);
                GlStateManager.translate(-width, 0, -height);
                break;
            case WEST:
                GlStateManager.rotate(-90, 0, 1, 0);
                GlStateManager.translate(0, 0, -height);
                break;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(0, 0, 0).tex(0, 0).normal(0, 1, 0).endVertex();
        buffer.pos(0, 0 , height).tex(0, 1).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0, height).tex(1, 1).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0, 0).tex(1, 0).normal(0, 1, 0).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
    }


}
