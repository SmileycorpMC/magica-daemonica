package net.smileycorp.magiadaemonica.common.rituals.summoning.trades;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class TradeUtils {

    private static final UUID COST = UUID.fromString("cdc852af-afe8-4eac-85bc-3f90e670a8da");
    private static final UUID BONUS = UUID.fromString("77f9ece5-8597-4b7a-8705-898598878e06");

    public static void addCostAttribute(EntityPlayer player, IAttribute attribute, double value) {
        IAttributeInstance attributes = player.getEntityAttribute(attribute);
        AttributeModifier modifier = attributes.getModifier(COST);
        if (modifier != null) value += modifier.getAmount();
        attributes.applyModifier(new AttributeModifier(COST, "magicadaemonicosts", value, 0));
    }

    public static void addBonusAttribute(EntityPlayer player, IAttribute attribute, double value) {
        IAttributeInstance attributes = player.getEntityAttribute(attribute);
        AttributeModifier modifier = attributes.getModifier(BONUS);
        if (modifier != null) value += modifier.getAmount();
        attributes.applyModifier(new AttributeModifier(BONUS, "magicadaemonibonus", value, 0));
    }

}
