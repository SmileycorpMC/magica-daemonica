package net.smileycorp.magiadaemonica.common.rituals.summoning;

import java.util.Locale;

public enum DemonTier {

    HIGH_PRINCE,
    PRINCE,
    ARCHBARRON,
    BARRON,
    ARCHLORD,
    GREATER_LORD,
    LESSER_LORD,
    GREATER_DEMON,
    LESSER_DEMON,
    GREATER_IMP,
    LESSER_IMP;

    private final String name;

    DemonTier() {
        this.name = name().toLowerCase(Locale.US);
    }

    public String getName() {
        return name;
    }
}
