package net.smileycorp.magiadaemonica.client.rituals;

import net.smileycorp.magiadaemonica.common.rituals.IRitual;

public interface RitualRenderer<T extends IRitual> {


    void render(T ritual, double x, double y, double z, int width, int height);

}
