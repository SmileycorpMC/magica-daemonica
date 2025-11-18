package net.smileycorp.magiadaemonica.client.rituals.renderers;

import net.smileycorp.magiadaemonica.common.rituals.Ritual;

public interface RitualRenderer<T extends Ritual> {


    void render(T ritual, double x, double y, double z, int width, int height);

}
