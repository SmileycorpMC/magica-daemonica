package net.smileycorp.magiadaemonica.client.rituals.renderers;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.smileycorp.magiadaemonica.common.rituals.Ritual;

public interface RitualRenderer<T extends Ritual> {


    void render(T ritual, float partialTicks);

    default void renderPlane( double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a, boolean doubleFaced) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        double dy = (y1 + y2) * 0.5;
        buffer.pos(x1, y1, z1).tex(0, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();
        buffer.pos(x1, dy, z2).tex(0, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
        buffer.pos(x2, y2, z2).tex(1, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
        buffer.pos(x2, dy, z1).tex(1, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();
        if (doubleFaced) {
            buffer.pos(x2, dy, z1).tex(1, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();
            buffer.pos(x2, y2, z2).tex(1, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
            buffer.pos(x1, dy, z2).tex(0, 1).color(r, g, b, a).normal(0, 1, 0).endVertex();
            buffer.pos(x1, y1, z1).tex(0, 0).color(r, g, b, a).normal(0, 1, 0).endVertex();
        }
        tessellator.draw();
    }

}
