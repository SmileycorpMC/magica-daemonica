package net.smileycorp.magiadaemonica.client.rituals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.magiadaemonica.common.rituals.summoningcircle.SummoningCircle;

public class SummoningCircleRenderer implements RitualRenderer<SummoningCircle> {

    @Override
    public void render(SummoningCircle ritual, int width, int height) {
        ResourceLocation name = ritual.getName();
        if (name == null) return;
        ResourceLocation texture = new ResourceLocation(name.getResourceDomain(), "textures/summoning_circles/" + name.getResourcePath() + ".png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        buffer.pos(0, 0.01, 0).tex(0, 0).normal(0, 1, 0).endVertex();
        buffer.pos(0, 0.01 , height).tex(0, 1).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0.01, height).tex(1, 1).normal(0, 1, 0).endVertex();
        buffer.pos(width, 0.01, 0).tex(1, 0).normal(0, 1, 0).endVertex();
        tessellator.draw();
    }


}
